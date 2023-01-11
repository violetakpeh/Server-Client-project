package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    Socket clientSocket;
    ObjectOutputStream output;
    ObjectInputStream input;
    String serverAddress = "127.0.0.1";

    public void run(){
        System.out.println(" Trying to connect to server");
        try {
            clientSocket = new Socket(serverAddress, 6000);
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Client establish I/O Stream");
            processConnection();
        }catch(UnknownHostException e){
            System.err.println(" Server Unavailable");
            System.exit(1);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeConnection();
        }


    }

    public void processConnection() {
        sendData(" Connection established with Client");
        String inputMessage = "";
        new Thread(){
            Scanner scanner = new Scanner(System.in);
            public void run(){
                while (true){
                    sendData(scanner.nextLine());
                }
            }

        }.start();
        do{
            try{
                inputMessage = (String) input.readObject();
                System.out.println(" Server : " + inputMessage);
            }catch (IOException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                System.err.println(" Object of an Unknown Type");
            }
        }while (!inputMessage.equals("QUIT"));
    }

    public void sendData(String s) {
        try{
            output.writeObject(s);
            output.flush();
            System.out.println("Client : " + s);
        }catch (IOException e){
            System.err.println("Error writing message");
        }
    }

    public void closeConnection() {
        try{
            output.close();
            input.close();
            clientSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
