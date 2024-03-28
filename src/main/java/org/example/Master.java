package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {
    private int port;
    private ServerSocket serverSocket;
    private Socket socket=new Socket();
    public Master() {
    }

    public void StartServer(){
        try{
            serverSocket=new ServerSocket(1234);
            while (true) {
                socket=serverSocket.accept();

                /* Handle the request */
                ActionsForClients handler=new ActionsForClients(socket);
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
    /*private Queue<Task> taskQueue;

    public void addTask(Task task) {
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notify(); // Notify waiting threads that a task is available
        }
    }

    public Task getTask() throws InterruptedException {
        synchronized (taskQueue) {
            while (taskQueue.isEmpty()) {
                taskQueue.wait(); // Wait until a task is available
            }
            return taskQueue.poll();
        }
    }*/


}
