package org.example;


import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ManagerApp extends Thread{

    public static void main(String[] args) {

        ArrayList<Room> rooms=new ArrayList<>();
        HashMap<String,ArrayList<Room>> Reservations=new HashMap<>();
        int number;
        do {
            Scanner sc=new Scanner(System.in);
            System.out.println("Please choose one of the following\n1)Add file\n2)Total Reservations by Region\n3)Exit");
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

                        ArrayList<Room> roomList1 = Reservations.get(room.getArea());
                        if (roomList1 == null) {
                            // Area not present, create new list and add the room
                            roomList1 = new ArrayList<>();
                            roomList1.add(room);
                            Reservations.put(room.getArea(), roomList1);
                        } else {
                            // Area present, add the room to the existing list
                            roomList1.add(room);
                        }

                    }
                    for(Room room:rooms){
                        new ManagerApp(room).start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(number==2) {
                String time;
                sc.nextLine();
                System.out.println("Enter Time Period(DD/MM/YYYY-DD/MM/YYY)");
                time=sc.nextLine();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US);

                // Split the input string into start and end date strings
                String[] parts = time.split("-");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid date range format. Expected format: dd/MM/yyyy-dd/MM/yyyy");
                }

                LocalDate startDate = LocalDate.parse(parts[0], formatter);
                LocalDate endDate = LocalDate.parse(parts[1], formatter);

                Reservations.forEach((area, roomList) -> {
                    int count = 0; // Counter for bookings within the specified period for the current area
                    for (Room room : roomList) {
                        // Check each booked period for overlaps with the specified period
                        for (String bookedPeriod : room.getBooked()) {
                            String[] dateParts = bookedPeriod.split("-");
                            LocalDate bookedStart = LocalDate.parse(dateParts[0], formatter);
                            LocalDate bookedEnd = LocalDate.parse(dateParts[1], formatter);

                            // Check for overlap between booked period and specified period
                            if (!(bookedEnd.isBefore(startDate) || bookedStart.isAfter(endDate))) {
                                count++;
                                break; // Assuming you count the room once for any overlap, remove break if counting all overlaps
                            }
                        }
                    }
                    System.out.println("Area: " + area + ", Reservations in period: " + count);
                });
            }
        } while (number != 3);

    }
    Socket requestSocket;

    ObjectInputStream in=null;
    ObjectOutputStream out=null;
    private Room room;
    private ArrayList<Room> Rooms;

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
