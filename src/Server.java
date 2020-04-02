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
    private final int MAXIMUM_CONNECTIONS = 2;
    private int connectionNum = 0;
    private ServerSocket server;

    private GameModel model;
    private ServerWorker players[] = new ServerWorker[2];

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

            // TODO: maybe initiate game model every time the second player connects (event after disconnect)
            // Initiate new game model
            model = new GameModel();

            try {
                // Wait for connection and create a new socket
                clientSocket = server.accept();

                // If connections is less than acceptable limit (2)
                // connect and create a client
                // else - close the socket
                if (connectionNum < MAXIMUM_CONNECTIONS) {
                    connectionNum++;

                    // Identify vacant player slots (0 or 1)
                    int playerID = players[0] == null ? 0 : 1;

                    // Create a client worker
                    ServerWorker client = new ServerWorker(clientSocket, this, playerID, model);

                    // Add client to the players array
                    players[playerID] = client;

                    // Create a client thread
                    new Thread(client).start();

                    System.out.println("New client connected");
                } else {
                    System.out.println("Refused client connection");
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void transmit(Data data) {
        players[0].dispatchData(data);
        players[1].dispatchData(data);
    }

    /**
     * Remove the player from the players list on disconnect
     * @param playerID
     */
    protected void onPlayerDisconnect(int playerID) {
        players[playerID] = null;
        connectionNum--;
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
