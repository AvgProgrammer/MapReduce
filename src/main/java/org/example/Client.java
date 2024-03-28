package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.UnknownHostException;

public class Client extends Thread{
    Socket requestSocket;
    ObjectInputStream in=null;
    ObjectOutputStream out=null;

    int a, b;
    Client(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public void run() {

        try {

            /* Create socket for contacting the server on port 4321*/
            requestSocket=new Socket("127.0.0.1",1234);
            /* Create the streams to send and receive data from server */
            this.out=new ObjectOutputStream(requestSocket.getOutputStream());
            this.in=new ObjectInputStream(requestSocket.getInputStream());
            /* Write the two integers */

            this.out.writeInt(this.a);
            this.out.flush();
            this.out.writeInt(this.b);
            this.out.flush();
            /* Print the received result from server */
            System.out.println("Server>" + in.readInt());

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
