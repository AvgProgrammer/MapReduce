package org.example;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Scanner;

public class ManagerApp extends Thread{
    public static void main(String[] args) {

        int number;
        do {
            Scanner sc=new Scanner(System.in);
            System.out.println("Please choose one of the following\n1)Add file\n2)Exit");
            number = sc.nextInt();
            if (number == 1) {
                System.out.println("Enter file path:");
                String path = sc.nextLine();
                    /*Gson gson = new Gson();
                    try (Reader reader = new FileReader("data.json")) {
                        Room room = gson.fromJson(reader, Room.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
            }
        } while (number != 2);

    }
    Socket requestSocket;
    ObjectInputStream in=null;
    ObjectOutputStream out=null;
    private Room room;

    public ManagerApp(Room room){
        this.room=room;
    }
    public void run() {

        try {

            requestSocket=new Socket("127.0.0.1",1234);

            this.out=new ObjectOutputStream(requestSocket.getOutputStream());
            this.in=new ObjectInputStream(requestSocket.getInputStream());

            this.out.writeObject(room);
            this.out.flush();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();	out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
