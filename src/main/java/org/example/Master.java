package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Master {

    private int port=1234;
    private int resultPort = 1235;
    ObjectInputStream in;
    private ServerSocket serverSocket;
    private ServerSocket resultServerSocket;
    private Socket socket=new Socket();
    private ArrayList<Worker> Workers=new ArrayList<>();
    private ArrayList<Room> aggregatedResults = new ArrayList<>();
    private final Object resultsLock = new Object();
    private int NumberOfWorkers;
    private int receivedResults = 0;

    public Master(int NumberOfWorkers){
        this.NumberOfWorkers = NumberOfWorkers;
    }
    public static void main(String[] args){
        Master masterServer=new Master(3);
        masterServer.startResultListener();
        masterServer.StartServer();
    }
    public int h(String name){
        int hashCode = name.hashCode();
        int numberOfWorkers = Workers.size();
        return Math.abs(hashCode) % numberOfWorkers;
    }
    public void StartServer(){
        try{
            serverSocket=new ServerSocket(port);
            System.out.println("Server connected.");
            for(int i=0;i<NumberOfWorkers;i++){
                Worker worker=new Worker();
                Workers.add(worker);
            }
            int workerCount=0;
            while (true) {
                socket=serverSocket.accept();
                System.out.println("Client connected.");
                Random rand = new Random();
                int number;
                if (NumberOfWorkers!=0) {
                    number = rand.nextInt(50) % NumberOfWorkers;
                }else{
                    number=0;
                }
                this.in = new ObjectInputStream(socket.getInputStream());
                Object receivedObject = in.readObject();
                changeWorker(receivedObject,socket);
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    public void changeWorker(Object receivedObject,Socket socket){
        try {
            if (receivedObject instanceof Room room) {

                int workerIndex = h(room.getRoomName());
                System.out.println("Room with room name:" + room.getRoomName() + " Added to worker:" + workerIndex);
                Workers.get(workerIndex).addTask(receivedObject,socket);

            } else if (receivedObject instanceof Filter filter) {
                System.out.println("Processing the filter in area:"+((Filter) receivedObject).getArea());

                synchronized (resultsLock) {
                    aggregatedResults.clear();
                    receivedResults = 0;
                }

                for (int j = 0; j < Workers.size(); j++) {
                    Map(j, filter,socket);
                }
                synchronized (resultsLock) {
                    while (receivedResults < Workers.size()) {
                        try {
                            resultsLock.wait(); // Wait for all results
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Waiting for results was interrupted.");
                            // Consider whether to break or continue waiting here
                        }
                    }
                    // Send aggregatedResults back to the client
                    ObjectOutputStream tenantOut = new ObjectOutputStream(socket.getOutputStream());
                    System.out.println(aggregatedResults.size());
                    tenantOut.writeObject(aggregatedResults);
                    tenantOut.flush();
                }

            }else if(receivedObject.toString().startsWith("BookedRoom:")){
                String[] parts = receivedObject.toString().split(":");
                forwardBookingToManagerApp(parts[1], parts[2], parts[3]);

            }
        }catch (IOException e) {
            System.err.println("Failed to change workers: " + e.getMessage());
        }
    }
    private void Map(int MapId,Filter filter,Socket socket){
            Workers.get(MapId).addTask(filter,socket);
    }

    private void forwardBookingToManagerApp(String roomName, String startDate, String endDate) {
        for(Worker worker:Workers){
            worker.setBooked( roomName, startDate, endDate);
        }
    }

    public void startResultListener() {
        new Thread(() -> {
            try {
                resultServerSocket = new ServerSocket(resultPort);
                System.out.println("Result listener started on port: " + resultPort);

                while (true) {
                    Socket workerSocket = resultServerSocket.accept();
                    System.out.println("Worker connected for result transmission.");

                    new Thread(() -> {
                        try (ObjectInputStream objectInputStream = new ObjectInputStream(workerSocket.getInputStream())) {
                            ArrayList<Room> workerResults = (ArrayList<Room>) objectInputStream.readObject();
                            synchronized (resultsLock) {
                                aggregatedResults.addAll(workerResults);
                                receivedResults++;
                                System.out.println("Results were sent " + aggregatedResults.size());
                                if (receivedResults == Workers.size()) {
                                    resultsLock.notifyAll(); // Notify that all results have been received
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error receiving results from worker: " + e.getMessage());
                        }
                    }).start();
                }
            } catch (Exception e) {
                System.err.println("Error in result listener: " + e.getMessage());
            }
        }).start();
    }

}
