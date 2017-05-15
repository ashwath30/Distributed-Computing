package project1;

import java.util.*;

class Process implements Runnable 
{
	 Master masterProcess;
	 Process process[];
	 volatile String message = "";
	 int parent_Process;
	 volatile Queue<Message> messageQueue = new LinkedList<Message>();
	 volatile Map<Integer,String> stats = new HashMap<Integer,String>();
	 
	 long distance;
	 boolean isComplete = false;
	 HashMap<Integer, Integer> nbrs;
	 int id = 0;
	 private Random random = new Random();

	public String getMsg() 
	{
		return message;
	}

	public void setMsg(String message) 
	{
		this.message = message;
	}
	
	public Process(int id, HashMap<Integer, Integer> nbrs, int leaderID, Master masterProcess) 
	{
		this.id = id;
		this.masterProcess = masterProcess;
		this.nbrs = new HashMap<>();
		this.nbrs = nbrs;
		if (leaderID == id) 
		{
			distance = 0;
			this.parent_Process = id;
		} 
		else 
		{
			distance = Integer.MAX_VALUE;
			this.parent_Process = -2;
		}
		
		for (Integer nbr : nbrs.keySet()) 
		
			stats.put(nbr, "unknown");
		
	}

	public void setProcessNeighbors(Process process[]) 
	{
		this.process = process;
	}
	 
	public synchronized Message change_msgList(Message message, boolean add) 
	{
		
		if (add) 
		{
			messageQueue.add(message);
			return message;
		} 
		else 
		
			return messageQueue.isEmpty() ? null : messageQueue.poll();
		
	}
	/*
	public static void breadthFirst(Integer[][] Xmatrix, int leaderID, int totalProcesses)
	{
		int dq ;                                          // Used to hold dequeued values
        Queue<Integer> Qqueue = new LinkedList<Integer>(); 
		HashMap<Integer, Integer> myparent  = new HashMap<>();
        // Visited array
        Boolean[] visited = new Boolean[Xmatrix.length] ;                
        Arrays.fill(visited,false);
		Integer[] dist = new Integer[totalProcesses];
        Qqueue.add(leaderID);         // add to queue
        visited[leaderID] = true ;   // mark as visited
		Arrays.fill(dist,0);
        // While the queue is not empty
		System.out.println("\nBFS Traversal order");
        while (!Qqueue.isEmpty()){
            dq = Qqueue.remove();          // Dequeue from queu
            System.out.printf("%d\t ",dq);  // print it

            // for every element in row of dequeued value dq
            for ( int z = 0 ; z < Xmatrix.length; z++ ){
                // If marked as adjacent and it is unvisited
                if ((Xmatrix[dq][z] == 1) && (!visited[z]) ){
                    Qqueue.add(z);             // add to queue
					myparent.put(z,dq);
					dist[z]=dist[dq] + 1;
					//mydistance.put(z,dist[dq]+1);
                    visited[z] = true ;       // mark as visited
                }
				
            }
        }
		System.out.println("");
		for (Integer name: myparent.keySet()){

            Integer key =name;
            Integer value = myparent.get(key); 
			System.out.println("\n");
            System.out.println(key + "'s parent is " + value + " and it's dist from source is "+dist[key]);  
		}
		masterProcess.setProcesses(process,myparent);
		
	} 
	*/
	
	public void run() 
	{
		masterProcess.roundCompletion(id);
		
		while (!isComplete) 
		{
			
			if (getMsg().equals("Start")) 
			{
				setMsg("");
				if (distance == 0) 
					sendMessages();
				
				masterProcess.roundCompletion(id);
			} 
			else if (getMsg().equals("Begin_Round")) 
			{
				setMsg("");
				boolean change = receiveMessages();
				if (change) 
				
					sendMessages();
				
				masterProcess.roundCompletion(id);
				
			} 
			else if (getMsg().equals("getParent")) 
			{
				// Sending Parent details to Master after completing the
				// algorithm
				setMsg("");
				int weight = (id == parent_Process) ? 0 : nbrs.get(parent_Process);
				
				masterProcess.setParents(id, parent_Process,weight);
				masterProcess.roundCompletion(id);
			} 
			else if (getMsg().equals("Completed")) 
			{
				// Terminating the Algorithm
				//System.out.println(id );
				//System.out.println("-------------------------------------------------------------------");
				/*System.out.println("Process ID: " + id +" \nParent of process "+id + " : " + parent_Process + " || Distance from Leader: " + distance);
				System.out.println("\n");*/
				isComplete = true;
			}
		}
	}

	// Processing messages in the Queue and updating the current distance
	private boolean receiveMessages() 
	{

		boolean change = false;
		Message msg= change_msgList(null,false);
		while (msg != null) 
		{
				long newDistance = msg.getDistance() + nbrs.get(msg.getfromProcess());
				if (newDistance < distance) 
				{ 
					change = true;
					this.distance = newDistance;
					if(parent_Process != -2)
						process[parent_Process].ackStats(id,"reject",false);
					
					this.parent_Process = msg.getfromProcess();
					process[id].ackStats(id,"unkonwn",true);
				}
				else
					process[msg.getfromProcess()].ackStats(id,"reject",false);
				
				msg = new Message();
				msg = change_msgList(null, false);
			}
		if(ACK(id)){
			if (parent_Process == id)
				Master.MSTcomplete=true;
		
			else
				process[parent_Process].ackStats(id, "done",false);
			
		}
		return change;
	}

	// Sending distance
	void sendMessages() 
	{
		Message msg = new Message();
		msg.setfromProcess(this.id);
		msg.setDistance(this.distance);
		for (int n : nbrs.keySet()) 
		{
			 //msg.nextInt((max - min) + 1) + min; //mkyoung
			msg.setTransmissionTime(random.nextInt((18 - 1) + 1) + 1);
			//System.out.println(
			if(msg!=null)
			process[n].change_msgList(msg, true);

		}
	}
	
	public synchronized boolean ackStats(int id, String reply, boolean rst){
		if(rst){
			for(Integer val : nbrs.keySet())
				stats.put(val,"unknown");
			
			return true;
		}else
			stats.put(id, reply);
		
		return true;
	}
	
	public synchronized boolean ACK(int id){
		for (Map.Entry<Integer, String> entry : stats.entrySet()) {
			if (entry.getKey() != parent_Process && entry.getValue().equals("unknown")) 
				return false;
			
		}
		return true;
	}

		

}

class Message 
{
	int ID;
	long round;
	private long transmissionTime;
	
	public int getfromProcess() 
	{
		return ID;
	}

	public void setfromProcess(int fromID) 
	{
		this.ID = fromID;
	}
	long currDistance;
	
	public long getDistance() 
	{
		return currDistance;
	}
	public void setDistance(long currDistance) 
	{
		this.currDistance = currDistance;
	}
	public long getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(long transmissionTime) {
		this.transmissionTime = transmissionTime;
	}
	
}
