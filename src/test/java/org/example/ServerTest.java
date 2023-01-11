package org.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerTest {
    @Test
    public void testServer() {
        Server server = new Server();
        Thread serverThread = new Thread(() -> server.run());
        serverThread.start();
        //Test client connecting to server
        try {
            Socket clientSocket = new Socket("localhost", 6000);
            ObjectOutputStream clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
            assertEquals("Connection established with server", clientInput.readObject());
            //Test sending data from client to server
            String message = "Hello, Server!";
            clientOutput.writeObject(message);
            clientOutput.flush();
            assertEquals("Client: " + message, clientInput.readObject());
            //Test sending data from server to client
            String serverMessage = "Hello, Client!";
            server.sendData(serverMessage);
            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
           assertEquals("Server: " + serverMessage, clientInput.readObject());
           });


            //Test closing the connection
            clientSocket.close();
           // assertTrue(clientSocket.isClosed());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            fail("Error: " + e.getMessage());
        }
    }
}

