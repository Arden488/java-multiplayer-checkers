/**
 * Server worker class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class does the server work to communicate with every client
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private Socket socket = null;
    private Server server = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;

    private GameModel model;
    private int playerID;

    /**
     * Constructor
     */
    protected ServerWorker(Socket socket, Server server, int playerID, GameModel model) {
        this.socket = socket;
        this.model = model;
        this.playerID = playerID;
        this.server = server;

        // Create input and output streams
        try {
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            inputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run method for the thread
     */
    public void run() {
        try {
            // TODO: Use event manager for handling input streams
            // Get an object from a client via input stream
            Data data = null;

            while ((data = (Data) inputStream.readObject()) != null) {
                handleEvent(data);
            }

            inputStream.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            this.server.onPlayerDisconnect(playerID);
            System.out.println("Client disconnected");
            e.printStackTrace();
        }
    }

    /**
     * Event handler method
     * Accepts data and reacts according to the data type
     * @param data
     */
    private void handleEvent(Data data) {
        String type = data.getType();

        switch (type) {
            default:
                this.server.transmit(data);
        }
    }

    protected void dispatchData(Data data) {
        try {
            outputStream.writeObject(data);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
