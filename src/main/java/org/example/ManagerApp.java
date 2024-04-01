package org.example;


import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ManagerApp extends Thread{
    public static void main(String[] args) {
        ArrayList<Room> rooms=new ArrayList<>();
        int number;
        do {
            Scanner sc=new Scanner(System.in);
            System.out.println("Please choose one of the following\n1)Add file\n2)Exit");
            number = sc.nextInt();
            if (number == 1) {
                JSONParser parser = new JSONParser();

                try {
                    Object obj = parser.parse(new FileReader("C:\\Users\\user\\IdeaProjects\\Kata2024\\src\\main\\java\\org\\example\\room_details_expanded.json"));
                    JSONObject jsonObject = (JSONObject) obj;

                    JSONArray roomList = (JSONArray) jsonObject.get("rooms");
                    Iterator<JSONObject> iterator = roomList.iterator();
                    while (iterator.hasNext()) {
                        JSONObject roomJson = iterator.next();
                        Room room = new Room(
                                (String) roomJson.get("roomName"),
                                ((Long) roomJson.get("noOfPersons")).intValue(),
                                (String) roomJson.get("area"),
                                ((Long) roomJson.get("stars")).doubleValue(),
                                ((Long) roomJson.get("noOfReviews")).intValue(),
                                (String) roomJson.get("roomImage")
                        );
                        rooms.add(room);
                        System.out.println(room.getRoomName());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (number != 2);
        for(Room room:rooms){
            new ManagerApp(room).start();
        }

    }
    Socket requestSocket;
    ObjectOutputStream out=null;
    private Room room;

    public ManagerApp(Room room){
        this.room=room;
    }
    public void run() {

        try {

            requestSocket=new Socket("localhost",1234);

            this.out=new ObjectOutputStream(requestSocket.getOutputStream());

            this.out.writeObject(room);
            this.out.flush();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
