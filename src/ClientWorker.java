/**
 * Client worker class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class does the client work to communicate with the server
 */

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientWorker extends SwingWorker<Void, Void> {
    private Client client = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;

    protected ClientWorker(Socket server, Client client) {
        this.client = client;

        try {
            outputStream = new ObjectOutputStream(server.getOutputStream());
            inputStream = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            System.out.println("You were disconnected from the server...");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Work method for the SwingWorker
     */
    protected Void doInBackground() {
        Data data = null;

        try {
            // TODO: Use event manager for handling input streams
            // Get an object from a client via input stream

            while ((data = (Data) inputStream.readObject()) != null) {
                this.client.handleEvent(data);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }

    /**
     * Send data to the server
     * @param data
     */
    protected void dispatchData(Data data) {
        try {
            outputStream.writeObject(data);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
