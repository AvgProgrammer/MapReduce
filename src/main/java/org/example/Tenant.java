package org.example;

import java.io.*;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class Tenant extends Thread{

    public static void main(String[] args){
        System.out.println("Hello Customer");
        int answer=0;
        String area="";
        String time="01/01/2024-05/01/2024";
        int numb=0;
        double price=0.0;
        double stars=0.0;
        Scanner sc=new Scanner(System.in);
        int answer2=0;
        do{
            do {
                System.out.println("Please Choose Filters:\n" +
                        "1)Area\n" +
                        "2)Time Period\n" +
                        "3)Number of tenants\n" +
                        "4)Price\n" +
                        "5)Stars");
                answer=sc.nextInt();
                sc.nextLine();
                switch (answer){
                    case 1:
                        System.out.println("Enter Area");
                        area=sc.nextLine();
                        break;
                    case 2:
                        System.out.println("Enter Time Period(DD/MM/YYYY-DD/MM/YYY)");
                        time=sc.nextLine();
                        sc.nextLine();
                        break;
                    case 3:
                        do{
                            System.out.println("Enter Number of tenants");
                            numb=sc.nextInt();
                            sc.nextLine();
                        }while(numb<=0);
                        break;
                    case 4:
                        do{
                            System.out.println("Enter Price");
                            price=sc.nextInt();
                            sc.nextLine();
                        }while (price<0);
                        break;
                    case 5:
                        do{
                            System.out.println("Enter Stars");
                            stars=sc.nextDouble();
                            sc.nextLine();
                        }while (stars<0.0 || stars>5.0);
                        break;
                }

            }while (answer<1 || answer>5);
            System.out.println("Do you want to enter another filter:\n1)Yes\n2)No");
            answer2= sc.nextInt();
        }while (answer2==1);
        Filter filter=new Filter(area,time,numb,price,stars);
        new Tenant(filter, sc).start();
    }
    Socket requestSocket;
    ObjectInputStream in=null;
    ObjectOutputStream out=null;
    private Filter filter;
    private Scanner scanner;

    public Tenant(Filter filter,Scanner scanner) {
        this.filter=filter;
        this.scanner = scanner;
    }

    public void book(Room room, LocalDate startDate, LocalDate endDate) {
        synchronized (room) {
            if (!room.isBooked(startDate, endDate)) {
                room.AddDate(startDate, endDate);
                System.out.println("Room " + room.getRoomName() + " successfully booked from " + startDate + " to " + endDate);
            } else {
                System.out.println("Room " + room.getRoomName() + " is not available for the selected dates.");
            }
        }
    }

    public void run() {

        try {

            requestSocket=new Socket("localhost",1234);

            this.out=new ObjectOutputStream(requestSocket.getOutputStream());

            this.out.writeObject(filter);
            this.out.flush();

            this.in = new ObjectInputStream(requestSocket.getInputStream());

            Object response = in.readObject();
            if (response instanceof ArrayList<?>) {
                ArrayList<Room> receivedRooms = (ArrayList<Room>) response;
                // Rest of the logic to handle received rooms, including booking
                if(receivedRooms.isEmpty()) {
                    System.out.println("No rooms found.");
                    return;
                } else {
                    for (int i = 0; i < receivedRooms.size(); i++) {
                        System.out.println((i + 1) + ") " + receivedRooms.get(i).getRoomName());
                    }
                }
                System.out.println("Enter the number of the room you wish to choose:");
                int choice=scanner.nextInt();

                if(choice < 1 || choice > receivedRooms.size()) {
                    System.out.println("Invalid choice. Exiting.");
                    return;
                }

                Room selectedRoom = receivedRooms.get(choice - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.US);

                // Split the input string into start and end date strings
                String[] parts =  this.filter.getTime().split("-");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid date range format. Expected format: dd/MM/yyyy-dd/MM/yyyy");
                }

                LocalDate startDate = LocalDate.parse(parts[0], formatter);
                LocalDate endDate = LocalDate.parse(parts[1], formatter);
                book(selectedRoom,startDate,endDate);
                System.out.println("You selected: " + selectedRoom.getRoomName());

                scanner.close();
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
