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

public class Client implements ServerSettings {
    // Instance variables
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

        // Set a worker link for the GameView and draw the UI
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
     * Accepts data and reacts according to the data type (ExchangeEvent)
     * @param data
     */
    protected void handleEvent(Data data) {
        ExchangeEvent type = data.getType();

        switch (type) {
            case NEW_ROUND:
                this.worker.setIsYourTurn(data);
                this.view.handleNewRound(data);
                break;
            case NEW_GAME:
                this.worker.setIsYourTurn(data);
                this.view.handleNewGame(data);
                break;
            case GAME_OVER:
                this.view.handleGameOver(data);
                break;
            case ASSIGN_PLAYER_ID:
                setWorkerPlayerID(data);
                break;
            case REQUEST_NEW_GAME:
                this.view.handleRequestNewGame();
                break;
            case WAITING_FOR_OPPONENT:
                this.view.handleAwaitingOpponent();
                break;
        }
    }

    /**
     * Set player ID received from the server to the worker instance
     * @param data
     */
    public void setWorkerPlayerID(Data data) {
        PlayerData player = (PlayerData) data.getPayload();
        int playerID = player.getPlayerID();
        this.worker.setPlayerID(playerID);
    }

    /**
     * Nested class
     * This class does the client work to communicate with the server
     */
    public class Worker extends SwingWorker<Void, Void> {
        // Instance variables
        private Client client = null;
        private ObjectInputStream inputStream = null;
        private ObjectOutputStream outputStream = null;
        private int playerID;
        private Boolean isYourTurn;

        /**
         * Constructor
         * Create and assign streams
         * @param server
         * @param client
         */
        protected Worker(Socket server, Client client) {
            this.client = client;

            try {
                outputStream = new ObjectOutputStream(server.getOutputStream());
                inputStream = new ObjectInputStream(server.getInputStream());
            } catch (IOException e) {
                System.out.println("You were disconnected from the server...");
                e.printStackTrace();

                // End the program if the client was disconnected by server
                System.exit(1);
            }
        }

        /**
         * Work method for the SwingWorker
         */
        protected Void doInBackground() {
            Data data = null;

            try {
                // Receive data from server and pass to the event handler method
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
         * Send a signal with a new game request to the server
         */
        protected void registerNewGameRequest() {
            Data dataToSend = new Data(ExchangeEvent.REQUEST_NEW_GAME);

            transmitData(dataToSend);
        }

        /**
         * Send a signal with a performed move to the server
         * @param fromRow
         * @param fromCol
         * @param toRow
         * @param toCol
         */
        protected void registerMove(int fromRow, int fromCol, int toRow, int toCol) {
            // Prepare a new MoveData object as it is allowed to transfer
            MoveData move = new MoveData(fromRow, fromCol, toRow, toCol, !isPlayingRed());

            Data dataToSend = new Data(ExchangeEvent.NEW_MOVE);
            dataToSend.setPayload(move);

            transmitData(dataToSend);
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

        /**
         * Current player ID getter
         * @return playerID
         */
        public int getPlayerID() {
            return playerID;
        }

        /**
         * Current player ID setter
         * @param playerID
         */
        public void setPlayerID(int playerID) {
            this.playerID = playerID;
        }

        /**
         * Getter with a boolean whether it
         * is the current player's turn
         * @return isYourTurn
         */
        public Boolean getIsYourTurn() {
            return isYourTurn;
        }

        // TODO: refactor
        /**
         * Setter for a boolean whether it
         * is the current player's turn
         * @param data
         */
        public void setIsYourTurn(Data data) {
            NewRoundData newRoundData = (NewRoundData) data.getPayload();
            Boolean state = getPlayerID() == newRoundData.getActivePlayerID();
            this.isYourTurn = state;
        }

        /**
         * Check if current player is RED
         * First connected player is always RED
         * @return Boolean
         */
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
