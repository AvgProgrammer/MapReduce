package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    private ArrayList<Room> rooms;

    public Worker(Socket connection) {
        try {
            this.in=new ObjectInputStream(connection.getInputStream());
            this.out=new ObjectOutputStream(connection.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            Object receivedObject = in.readObject();
            if (receivedObject instanceof Room) {
                Room room = (Room) receivedObject;
                rooms.add(room);
            }
            else if (receivedObject instanceof Filter) {
                Filter filter = (Filter) receivedObject;

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
