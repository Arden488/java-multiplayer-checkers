/**
 * Client class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class performs client connection, processes user interactions and draws the board
 */

import java.io.IOException;
import java.net.Socket;

public class Client {
    private final int PORT = 5678;
    private final String HOST = "127.0.0.1";
    private Socket server = null;
    private GameView view = null;

    /**
     * Constructor
     */
    public Client() {
        connect();

        // Create a client worker and run it
        ClientWorker worker = new ClientWorker(server, this);
        worker.execute();

        this.view = new GameView(worker);
    }

    /**
     * Connect method
     * Performs connection to the server
     */
    private void connect() {
        try {
            server = new Socket(HOST, PORT);
            System.out.println("Connected to the server at: " + HOST + ":" + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Event handler method
     * Accepts data and reacts according to the data type
     * @param data
     */
    protected void handleEvent(Data data) {
        String type = data.getType();

        switch (type) {
            case "test":
                view.test((TestData) data.getPayload());
                break;
            default:
                System.out.println(type);
        }
    }

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        new Client();
    }
}
