package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Reducer {
    private ServerSocket serverSocket;
    private int NumberOfWorkers=0;
    private Socket socket=new Socket();
    private int port=1236;
    private Socket requestSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ArrayList<Room> Rooms=new ArrayList();
    public static void main(String[] args) {
        Reducer reducer=new Reducer();
        reducer.StartReducer();
    }
    private class ReducerThread extends Thread{

        private ArrayList<Room> filterdRooms;
        private ArrayList<Room> rooms;
        private SharedResult sharedResult;

        public ReducerThread(ArrayList<Room> filterdRooms,ArrayList<Room> Rooms,SharedResult sharedResult){
            this.rooms=Rooms;
            this.filterdRooms=filterdRooms;
            this.sharedResult=sharedResult;
        }

        @Override
        public void run() {
            ArrayList<Room> newFilteredRooms = new ArrayList<>(filterdRooms);
            for(Room room:rooms){
                boolean found = false;
                for (Room filteredRoom : filterdRooms) {
                    if (filteredRoom.getRoomName().equals(room.getRoomName()) && filteredRoom.getArea().equals(room.getArea())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newFilteredRooms.add(room);
                }
            }
            sharedResult.setResult(newFilteredRooms);
        }
    }
    public class SharedResult {
        private ArrayList<Room> result; // Field to store the result

        // Method to set the result
        public synchronized void setResult(ArrayList<Room> result) {
            this.result = result;
        }

        // Method to get the result
        public synchronized ArrayList<Room> getResult() {
            return result;
        }
    }

    private void StartReducer(){
        try {
            serverSocket=new ServerSocket(port);
            System.out.println("Server connected.");
            int count=0;
            ArrayList<Room> filteredRooms=new ArrayList<>();;
            while (true) {
                socket=serverSocket.accept();
                System.out.println("Worker is connected.");
                this.in=new ObjectInputStream(socket.getInputStream());
                NumberOfWorkers=in.readInt();
                int FilterOrNot=in.readInt();
                ArrayList<Room> rooms= (ArrayList<Room>) in.readObject();
                if(count<NumberOfWorkers){
                    SharedResult sharedResult=new SharedResult();
                    System.out.println(filteredRooms.size());
                    ReducerThread thread=new ReducerThread(filteredRooms,rooms,sharedResult);
                    thread.start();thread.join();
                    filteredRooms=sharedResult.getResult();
                    System.out.println(filteredRooms.size());
                    count++;
                }
                if(count>=NumberOfWorkers){
                    System.out.println("Feugo gia Master");
                    System.out.println(filteredRooms.size());
                    count=0;
                    Socket masterSocket = new Socket("localhost", 1235);
                    ObjectOutputStream outToMaster = new ObjectOutputStream(masterSocket.getOutputStream());
                    System.out.println("trying to connect to Master");
                        // Send results. You might need to customize this part based on your application logic.
                    outToMaster.writeObject(filteredRooms); // Example of sending filtered rooms
                    outToMaster.flush();
                    outToMaster.writeInt(FilterOrNot); // Example of sending filtered rooms
                    outToMaster.flush();
                    outToMaster.close();
                    masterSocket.close();
                    filteredRooms.clear();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
