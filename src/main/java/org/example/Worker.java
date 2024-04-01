package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Worker{
    private ArrayList<Room> Rooms;
    private Socket connection;
    private final HashMap<Socket,Object> taskQueue=new HashMap<>();
    private final Object lock = new Object();
    private final ArrayList<WorkerThread> workerThreads ;
    private final int threadCount = 5;

    public Worker() {
        this.Rooms=new ArrayList<>();
        this.workerThreads=new ArrayList<>();
    }

    public void addTask(Object task,Socket socket){
        if(workerThreads.size()<threadCount){
            WorkerThread thread=new WorkerThread(socket);
            workerThreads.add(thread);
            thread.start();
            taskQueue.put(socket,task);
        }else{
            synchronized (lock) {
                taskQueue.put(socket,task);
                lock.notify(); // Notify one of the waiting threads that a task is available
            }
        }
    }

    private class WorkerThread extends Thread {
        private Socket connection;
        ObjectInputStream in;
        ObjectOutputStream out;

        public WorkerThread(Socket connection){
                this.connection=connection;
        }
        public void run() {
            while (!isInterrupted()) {
                Map.Entry<Socket, Object> taskEntry = null;
                synchronized (lock) {
                    while (taskQueue.isEmpty()) {
                        try {
                            lock.wait(); // Wait for a new task
                        } catch (InterruptedException e) {
                            interrupt(); // Preserve interrupt status
                            return; // Exit on interruption
                        }
                    }
                    taskEntry = taskQueue.entrySet().iterator().next(); // Retrieve the next task
                    taskQueue.remove(taskEntry.getKey()); // Remove the task from the queue
                }
                processTask(taskEntry.getValue()); // Process the task using its value
            }
        }
        private void processTask(Object task) {
            if (task instanceof Room room) {
                Rooms.add(room);
                System.out.println("Add the room");
                System.out.println(Rooms.size());
                // Further processing for Room
            } else if (task instanceof Filter filter) {
                // Implement filter processing and response
                System.out.println("Filter the rooms");
                try {
                    ArrayList<Room> filteredRooms = filteredRooms(filter);
                        // Assuming 'masterAddress' is the IP of the Master and 'masterPort' is the port it listens on for results
                        Socket masterSocket = new Socket("localhost", 1235);
                        ObjectOutputStream outToMaster = new ObjectOutputStream(masterSocket.getOutputStream());
                        // Send results. You might need to customize this part based on your application logic.
                        outToMaster.writeObject(filteredRooms); // Example of sending filtered rooms
                        outToMaster.flush();
                        outToMaster.close();
                        masterSocket.close();
                } catch (IOException e) {
                    System.out.println("Error processing filter task: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000); // Simulate processing time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private ArrayList<Room> filteredRooms(Filter filter) {
        ArrayList<Room> FilteredRooms = new ArrayList<>();
        FilteredRooms = Rooms;
        return FilteredRooms;
    }
}
