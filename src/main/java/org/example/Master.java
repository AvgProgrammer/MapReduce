package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Master {

    private int port=1234;
    private int resultPort = 1235;
    ObjectInputStream in;
    ObjectOutputStream out;
    private ServerSocket serverSocket;
    private ServerSocket resultServerSocket;
    private Socket socket=new Socket();
    private final ArrayList<Room> aggregatedFilters = new ArrayList<>();
    private final ArrayList<Room> aggregatedRooms = new ArrayList<>();
    private final Object resultsFilter = new Object();
    private final Object resultsRoom = new Object();
    private final int NumberOfWorkers;
    private int receivedFilter = 0;
    private int receivedRoom = 0;
    private ArrayList<Socket> SocketToWorker=new ArrayList<>();

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
        int numberOfWorkers = SocketToWorker.size();
        return Math.abs(hashCode) % SocketToWorker.size();
    }
    public void StartServer(){
        try{
            serverSocket=new ServerSocket(port);
            System.out.println("Server connected.");
            int workerCount=0;
            while (true) {
                socket=serverSocket.accept();
                System.out.println("Client connected.");
                this.in = new ObjectInputStream(socket.getInputStream());
                int num = in.readInt();
                if(num!=6 && num!=0) {
                    Object receivedObject = in.readObject();
                    changeWorker(receivedObject, num);
                } else if (num==6) {
                    System.out.println("Worker connected.");
                   if(SocketToWorker.size()<NumberOfWorkers) {
                        SocketToWorker.add(socket);
                        System.out.println(SocketToWorker.size());
                   }
                }

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
    public void changeWorker(Object receivedObject, int num){
        try {
            if (receivedObject instanceof Room room && num==1) {

                int workerIndex = h(room.getRoomName());
                System.out.println("Room with room name:" + room.getRoomName() + " Added to worker:" + workerIndex);
                Socket socket1=SocketToWorker.get(workerIndex);
                ObjectOutputStream outWorker=new ObjectOutputStream(socket1.getOutputStream());

                outWorker.writeObject(receivedObject);
                outWorker.flush();

                outWorker.writeInt(SocketToWorker.size());
                outWorker.flush();
            } else if (receivedObject instanceof Filter filter) {
                System.out.println("Processing the filter in area:"+((Filter) receivedObject).getArea());

                synchronized (resultsFilter) {
                    aggregatedFilters.clear();
                    receivedFilter = 0;
                }

                for (int j = 0; j < SocketToWorker.size(); j++) {
                    Map(j, filter);
                }
                synchronized (resultsFilter) {
                    while (receivedFilter < 1) {
                        try {
                            resultsFilter.wait(); // Wait for all results
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Waiting for results was interrupted.");
                            // Consider whether to break or continue waiting here
                        }
                    }
                    // Send aggregatedResults back to the client
                    ObjectOutputStream tenantOut = new ObjectOutputStream(socket.getOutputStream());
                    tenantOut.writeObject(aggregatedFilters);
                    tenantOut.flush();
                }

            }
            if (num == 2){
                ArrayList<Room> listOfArrays = new ArrayList<>();

                /*for (Worker worker: Workers){
                    listOfArrays.addAll( worker.getWorkers());
                }
                */
                synchronized (resultsRoom) {
                    aggregatedRooms.clear();
                    receivedRoom = 0;
                }
                System.out.println("Eimai edw");
                for (int i = 0; i < SocketToWorker.size(); i++) {
                    Map(i, listOfArrays);
                    System.out.println("Esteila");
                }
                synchronized (resultsRoom) {
                    while (receivedRoom < 1) {
                        try {
                            resultsRoom.wait(); // Wait for all results
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Waiting for results was interrupted.");
                            // Consider whether to break or continue waiting here
                        }
                    }
                    // Send aggregatedResults back to the client
                    ObjectOutputStream tenantOut = new ObjectOutputStream(socket.getOutputStream());
                    tenantOut.writeObject(aggregatedRooms);
                    tenantOut.flush();
                }
            }else if(num==4){
                for (int i = 0; i < SocketToWorker.size(); i++) {
                    Socket socket1=SocketToWorker.get(i);
                    ObjectOutputStream outWorker=new ObjectOutputStream(socket1.getOutputStream());
                    System.out.println("Sending Results to map");

                    outWorker.writeObject(receivedObject);
                    outWorker.flush();

                    outWorker.writeInt(SocketToWorker.size());
                    outWorker.flush();
                    System.out.println("Esteila");
                }

            }
        }catch (IOException e) {
            System.err.println("Failed to change workers: " + e.getMessage());
        }
    }
    private void Map(int MapId,Object Object) throws IOException {
        Socket socket1=SocketToWorker.get(MapId);
        ObjectOutputStream outWorker=new ObjectOutputStream(socket1.getOutputStream());
        System.out.println("Sending Results to map");

        outWorker.writeObject(Object);
        outWorker.flush();

        outWorker.writeInt(SocketToWorker.size());
        outWorker.flush();
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
                            ArrayList<Room> Results = (ArrayList<Room>) objectInputStream.readObject();
                            int number=objectInputStream.readInt();
                            if(number==0){
                            synchronized (resultsFilter) {
                                aggregatedFilters.addAll(Results);
                                receivedFilter++;
                                System.out.println("Results were sent " + aggregatedFilters.size());
                                if (receivedFilter == 1) {
                                    resultsFilter.notifyAll(); // Notify that all results have been received
                                }
                            }
                            }else{
                                synchronized (resultsRoom) {
                                    aggregatedRooms.addAll(Results);
                                    receivedRoom++;
                                    System.out.println("Results were sent " + aggregatedRooms.size());
                                    if (receivedRoom == 1) {
                                        resultsRoom.notifyAll(); // Notify that all results have been received
                                    }
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
