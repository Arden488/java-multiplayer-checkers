/**
 * Client class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class performs client connection, processes user interactions and draws the board
 */

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private final int PORT = 5678;
    private final String HOST = "127.0.0.1";
    private Socket server = null;
    private Worker worker = null;
    private GameView view = null;

    /**
     * Constructor
     */
    public Client() {
        connect();

        this.view = new GameView();

        // Create a client worker and run it
        worker = new Worker(server, this);
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
     * Inner class
     * This class does the client work to communicate with the server
     */
    public class Worker extends SwingWorker<Void, Void> {
        private Client client = null;
        private ObjectInputStream inputStream = null;
        private ObjectOutputStream outputStream = null;
        private int playerID;
        private Boolean isYourTurn;

        protected Worker(Socket server, Client client) {
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

        protected void registerNewGameRequest() {
            Data dataToSend = new Data("REQUEST_NEW_GAME");

            dispatcher(dataToSend);
        }

        protected void registerMove(int fromRow, int fromCol, int toRow, int toCol) {
            MoveData move = new MoveData(fromRow, fromCol, toRow, toCol, !isPlayingRed());

            Data dataToSend = new Data("MOVE");
            dataToSend.setPayload(move);

            dispatcher(dataToSend);
        }

        protected void dispatcher(Data data) {
            switch (data.getType()) {
                case "REQUEST_NEW_GAME":
                case "MOVE":
                    transmitData(data);
            }
        }

        /**
         * Send data to the server
         * @param data
         */
        protected void transmitData(Data data) {
            try {
                outputStream.writeObject(data);
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }

        public int getPlayerID() {
            return playerID;
        }

        public void setPlayerID(int playerID) {
            this.playerID = playerID;
        }

        public Boolean getIsYourTurn() {
            return isYourTurn;
        }

        // TODO: refactor
        public void setIsYourTurn(Data data) {
            NewRoundData newRoundData = (NewRoundData) data.getPayload();
            Boolean state = getPlayerID() == newRoundData.getActivePlayerID();
            this.isYourTurn = state;
        }

        public Boolean isPlayingRed() {
            return playerID == 0;
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
