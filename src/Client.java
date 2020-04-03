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
    private ClientWorker worker = null;
    private GameView view = null;

    /**
     * Constructor
     */
    public Client() {
        connect();

        this.view = new GameView();

        // Create a client worker and run it
        worker = new ClientWorker(server, this);
        worker.execute();

        this.view.setWorker(worker);
        this.view.drawLayout();
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

        // TODO: use common class to avoid repetition
        switch (type) {
            case "NEW_ROUND":
                this.worker.setIsYourTurn(data);
                this.view.handleNewRound(data);
                break;
            case "NEW_GAME":
                this.worker.setIsYourTurn(data);
                this.view.handleNewGame(data);
                break;
            case "GAME_OVER":
                this.view.handleGameOver(data);
                // TODO: handle game over data
                break;
            case "ASSIGN_PLAYER_ID":
                setPlayerID(data);
                break;
            case "REQUEST_NEWGAME":
                this.view.handleRequestNewGame();
                break;
            case "WAITING_FOR_OPPONENT":
                this.view.handleAwaitingOpponent();
                break;
        }
    }

    public void setPlayerID(Data data) {
        PlayerData player = (PlayerData) data.getPayload();
        int playerID = player.getPlayerID();
        this.worker.setPlayerID(playerID);
    }

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        new Client();
    }
}
