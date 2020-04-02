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
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;

    public ClientWorker(Socket server) {
        try {
            outputStream = new ObjectOutputStream(server.getOutputStream());
            inputStream = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Work method for the SwingWorker
     */
    public Void doInBackground() {
        Object data = null;

        try {
            // TODO: Use event manager for handling input streams
            // Get an object from a client via input stream
            data = (Object) inputStream.readObject();

            while (data != null) {
                System.out.println(data);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }
}
