package org.example;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server {
    private ServerSocket serverSocket;
    public Socket connection;
    ObjectOutputStream output;
    private ObjectInputStream input;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Server() {
        try {
            serverSocket = new ServerSocket(6000, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        System.out.println("Server is ready to accept connection");
        try {
            connection = serverSocket.accept();
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
           // getIOStreams();
            processConnection();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }


    private void processConnection() throws ClassNotFoundException, IOException {
        sendData("Connection established with server");
        String inputMessage;
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    sendData(scanner.nextLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        do {
            inputMessage = (String) input.readObject();
            System.out.println("Client: " + inputMessage);
        } while (!inputMessage.equals("QUIT"));
    }
    void sendData(String s) throws IOException {
        output.writeObject(s);
        output.flush();
        System.out.println("Server: " + s);
    }
    private void closeConnection() {
        try {
            output.close();
            input.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}





