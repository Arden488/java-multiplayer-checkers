/**
 * Server class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class creates multiple threads to handle connecting clients
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    // Port to connect
    private final int PORT = 5678;
    private ServerSocket server;

    private GameModel model;

    /**
     * Constructor
     */
    public Server() {
        try {
            // Create server socket to wait for connections
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run method for the thread
     */
    public void run() {
        while (true) {
            Socket clientSocket = null;

            // Initiate new game model
            model = new GameModel();

            try {
                // Wait for connection and create a new client
                clientSocket = server.accept();
                System.out.println("New client connected");

                // Create a client worker
                ServerWorker client = new ServerWorker(clientSocket, model);

                // Create a client thread
                new Thread(client).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        // Create and start a thread for server
        Thread thread = new Thread(new Server());
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
