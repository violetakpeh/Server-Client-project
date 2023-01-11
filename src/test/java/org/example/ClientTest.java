package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ClientTest {
    @Test
    public void testClient() {
        Client client = new Client();
        Thread clientThread = new Thread(() -> client.run());
        clientThread.start();
        //Test server connecting to client
        try {
            ServerSocket serverSocket = new ServerSocket(6000,100);
            Socket connection = new Socket();
            ObjectOutputStream serverOutput = new ObjectOutputStream(connection.getOutputStream());
            ObjectInputStream serverInput = new ObjectInputStream(connection.getInputStream());
            assertEquals("Connection established with server", serverInput.readObject());
            //Test sending data from server to client
            String message = "Hello, Server!";
            serverOutput.writeObject(message);
            serverOutput.flush();
            assertEquals("Client: " + message, serverInput.readObject());
            //Test sending data from server to client
            String serverMessage = "Hello, Client!";
            client.sendData(serverMessage);
            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                assertEquals("Server: " + serverMessage, serverInput.readObject());
            });


            //Test closing the connection
            connection.close();
            // assertTrue(clientSocket.isClosed());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            fail("Error: " + e.getMessage());
        }
    }
}
