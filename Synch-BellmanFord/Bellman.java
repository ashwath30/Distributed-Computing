/*
 * Project 1: Synchronous Bellman Ford Algorithm
 * 
 * Authors  : Deepak Shanmugam, Ashwath Santhanam, Haripriyaa U Manian
 * 
 */

package project1;

import java.util.*;
import java.io.*;
import java.lang.*;

public class Bellman 
{
	public static ArrayList<Integer> nodesList = new ArrayList<Integer>();
	public static void main(String[] input)  
	{
		
		int totalProcesses=0;
		int leaderID=-1;
		int wt,i,j;
		i=0;
		HashMap<Integer, Integer> newmap  = new HashMap<>();
		
		HashMap<Integer, Integer> myparent  = new HashMap<>();
		//HashMap<Integer, Integer> mydistance  = new HashMap<>();
		
		
		if (input.length != 1)
		{
			System.out.println("Usage: java -cp . project1.Bellman <inputFiepath>");
			System.exit(0);
		}
		try 
		{
			File f = new File(input[0]);
			Scanner scanner = new Scanner(f);

		while(scanner.hasNextLine())
		{
		String line=scanner.nextLine();
		if(line.startsWith("#"))
		{	
			if(line.contains("number of nodes"))
				 totalProcesses = scanner.nextInt();
			
			else if(line.contains("node IDs"))
			{
				for(int k=0;k<totalProcesses;k++)
					nodesList.add(scanner.nextInt());
			}

			else if(line.contains("id of the leader"))
				leaderID = scanner.nextInt();
			
			else if(line.contains("means there is no edge"))
			{
			Master masterProcess = new Master(totalProcesses);
			Process process[]=new Process[totalProcesses + 1];
			Process process1[];
			
			Integer[][] Xmatrix = new Integer[totalProcesses][totalProcesses];                 
			for (Integer[] row: Xmatrix)
				Arrays.fill(row, -1);
			
			Integer[] dist = new Integer[totalProcesses];
			
			while(i < totalProcesses)
			{
				//System.out.println("");
				//System.out.println("Neighrours of Process "+i);
				//neighbors_hashmap
				int tempI=nodesList.get(i);
				HashMap<Integer, Integer> nbrs  = new HashMap<>();
				j=0;
				
				while(j< totalProcesses)
				{
					int tempJ=nodesList.get(j);
					wt = scanner.nextInt();
					if (wt != -1) 
					{
						nbrs.put(tempJ, wt);
						newmap.put(i,j);
						Xmatrix[i][tempJ] = wt;
					}
						
					j++;
					
				}
				
				/*for (Integer name: nbrs.keySet()){
			
					String key =name.toString();
					System.out.println(key); 
				}*/
				process[i] = new Process(tempI, nbrs, leaderID, masterProcess);
				i++;
			}
			
		//Process.breadthFirst(Xmatrix,leaderID,totalProcesses);
		
		
		
       int dq ;                                          // Used to hold dequeued values
        Queue<Integer> Qqueue = new LinkedList<Integer>(); 

        // Visited array
        Boolean[] visited = new Boolean[Xmatrix.length] ;                
        Arrays.fill(visited,false);

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
    //}
	
	
	
	System.out.println("");
		for (Integer name: myparent.keySet()){

            Integer key =name;
            Integer value = myparent.get(key); 
			System.out.println("\n");
            System.out.println(key + "'s parent is " + value + " and it's dist from source is "+dist[key]);  
			


} 
		
//Form the result matrix here
		/*int resultmatrix[][] = new int[totalProcesses][totalProcesses];
			for (int[] row: resultmatrix)
				Arrays.fill(row, -1);
			
		for (int i1 = 0; i1 < totalProcesses; i1++) 
		{		
				for (int j1 = 0; j1 < totalProcesses; j1++) 
				{
					
					if(myparent.get(i1)!=null && (i1 != j1) && (myparent.get(i1) == j1) ){
					resultmatrix[i1][j1]=1;
					resultmatrix[j1][i1]=1;
					}
				}
		}*/
			
		
//Display the resultmatrix here		
		/*System.out.println("\n\n////-------------------Adjacency Matrix---------------//// ");
		System.out.println("\n");
		System.out.print("\t0" );
		for (int j2 = 1; j2 < totalProcesses; j2++) 
				System.out.print("\t"+j2);
				
		System.out.println("");
		
		for (int j2 = 0; j2 < totalProcesses; j2++) 
				System.out.print("\t__");
				
		for (int i2 = 0; i2 < totalProcesses; i2++) 
		{
			System.out.println("\n");
			System.out.print(i2+" | ");
			
			for (int j2 = 0; j2 < totalProcesses; j2++) 
				System.out.print("\t"+resultmatrix[i2][j2]);	
		}
		System.out.println("\n");*/
		
		
		
		
		
			
			Master.setProcesses(process,myparent,totalProcesses);
			// Start Master Thread
			Thread masterThread = new Thread(masterProcess);
			masterThread.start();
			// Start all Processes threads
			i=0;
			while(i < totalProcesses)
			
			{
				Thread processThread = new Thread(process[i]);
				processThread.start();
				i++;
			}
			}
		}
		}
		
		scanner.close();
		
		}
		
		catch (IOException e) 
		{
			System.out.println("File not found");
			e.printStackTrace();
		}
		
	}
}

/*
		public void breadthFirst(Integer[][] matrix, int startVertex) {
        /* ----------------------------------------------------------------------------------------------------------------
         * Declaration/Initialization
         * ---------------------------------------------------------------------------------------------------------------- 
        int dq ;                                          // Used to hold dequeued values
        Queue<Integer> queue = new LinkedList<Integer>(); // Create instance of Queue

        // Visited array
        Boolean[] visited = new Boolean[matrix.length] ;                 // Array used to keep track of visited vertexes
        Arrays.fill(visited,false);

        /* ----------------------------------------------------------------------------------------------------------------
         * Breadth First Algorithm Iterative Implementation
         * ---------------------------------------------------------------------------------------------------------------- 
        queue.add(startVertex);         // add to queue
        visited[startVertex] = true ;   // mark as visited

        // While the queue is not empty
        while (!queue.isEmpty()){
            dq = queue.remove();          // Dequeue from queu
            System.out.printf("%d ",dq);  // print it

            // for every element in row of dequeued value dq
            for ( int j = 0 ; j < matrix.length; j++ ){
                // If marked as adjacent and it is unvisited
                if ((matrix[dq][j] == 1) && (!visited[j]) ){
                    queue.add(j);             // add to queue
                    visited[j] = true ;       // mark as visited
                }
            }
        }
    }*/