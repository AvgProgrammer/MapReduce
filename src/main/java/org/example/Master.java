package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Master {
    private int port;
    private ServerSocket serverSocket;
    private Socket socket=new Socket();


    public static void main(String[] args){
        new Master().StartServer();
        //int numWorkers = readConfiguration(args);

        /*for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker(i);
            worker.start();
        }*/
    }
    public void StartServer(){
        try{
            serverSocket=new ServerSocket(1234);
            while (true) {
                socket=serverSocket.accept();

                /* Handle the request */
                ActionsForClients handler = new ActionsForClients(socket);
                handler.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
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
    public static int readConfiguration(String[] args) {
        if (args.length < 1) {
            System.err.println("Configuration file path is missing.");
            System.exit(1);
        }

        String configFilePath = args[0];
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Retrieve the value associated with the parameter specifying the number of Worker nodes
        String numWorkersStr = properties.getProperty("numWorkers");

        if (numWorkersStr == null) {
            System.err.println("Number of Worker nodes not specified in the configuration file.");
            System.exit(1);
        }

        // Convert the value to an integer and return it
        try {
            return Integer.parseInt(numWorkersStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid value for number of Worker nodes.");
            System.exit(1);
        }

        // In case of any errors, exit the program
        return 0;
    }
}
