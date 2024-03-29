package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Scanner;

public class Tenant extends Thread{

    public static void main(String[] args){
        System.out.println("Hello Customer");
        int answer=0;
        String area="";
        String time="";
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
                        System.out.println("Enter Time Period(DD-MM-YYYY-DD-MM-YYY)");
                        time=sc.nextLine();
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
        sc.close();
    }
    Socket requestSocket;
    ObjectInputStream in=null;
    ObjectOutputStream out=null;
    private Filter filter;

    Tenant(Filter filter) {
        this.filter=filter;
    }

    public void run() {

        try {

            /* Create socket for contacting the server on port 4321*/
            requestSocket=new Socket("127.0.0.1",1234);
            /* Create the streams to send and receive data from server */
            this.out=new ObjectOutputStream(requestSocket.getOutputStream());
            this.in=new ObjectInputStream(requestSocket.getInputStream());
            /* Write the two integers */

            this.out.writeObject(filter);
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
