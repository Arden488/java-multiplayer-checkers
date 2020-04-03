/**
 * Server class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class creates multiple threads to handle connecting clients
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable, ServerSettings {
    // Class constants
    private final int REQUIRED_PLAYERS = 2;

    // Instance variables
    private ServerSocket server;
    private ClientWorker players[] = new ClientWorker[2];
    // TODO: reset after player disconnect
    private int playersReady = 0;
    private int numberOfConnections = 0;

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

            try {
                // Wait for connection and create a new socket
                clientSocket = server.accept();

                // If connections is less than required number of players
                // connect and create a client
                // else - close the socket
                if (numberOfConnections < REQUIRED_PLAYERS) {
                    numberOfConnections++;

                    // Identify vacant player slots (0 or 1)
                    int playerID = players[0] == null ? 0 : 1;

                    // Create a client worker
                    ClientWorker client = new ClientWorker(clientSocket, this, playerID);
                    // Add client to the players array
                    players[playerID] = client;

                    // Create a client thread
                    new Thread(client).start();

                    // Send playerID to the client
                    client.sendPlayerID();

                    // Send a signal to wait for another client
                    client.sendAwaitAnotherPlayer();

                    // If there is a proper number of players - send a signal
                    // to allow the request for a new game
                    if (numberOfConnections == REQUIRED_PLAYERS) {
                        for (int i = 0; i < 2; i++) {
                            players[i].sendEnoughPlayers();
                        }
                    }

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

    /**
     * Transmit data to the client via corresponding ClientWorker
     * @param targetPlayerID
     * @param data
     */
    protected void transmit(int targetPlayerID, Data data) {
        // Find the ClientWorker by player id
        ClientWorker targetPlayer = getPlayerByID(targetPlayerID);

        targetPlayer.transmitData(data);
    }

    /**
     * Remove the player from the players list on disconnect
     * @param playerID
     */
    protected void onPlayerDisconnect(int playerID) {
        players[playerID] = null;
        numberOfConnections--;
        playersReady--;
    }

    /**
     * Event handler when a player requests a new game
     * Increment a number of "ready" players and check
     * if there is a sufficient number of players -
     * create a game model and send a signal to start new game
     */
    public void onPlayerRequestNewGame() {
        playersReady++;

        if (playersReady == REQUIRED_PLAYERS) {
            startNewGame();
        }

    }

    /**
     * Set a model for a ClientWorker and send a signal to start a new game
     */
    private void startNewGame() {
        // Initiate new game model
        GameModel model = new GameModel();

        // Do for both players
        for (int i = 0; i < 2; i++) {
            players[i].setModel(model);
            players[i].sendNewGame();
        }
    }

    /**
     * playersReady counter setter
     * @param playersReady
     */
    public void setPlayersReady(int playersReady) {
        this.playersReady = playersReady;
    }

    /**
     * ClientWorker getter by player ID
     * @param playerID
     * @return ClientWorker
     */
    private ClientWorker getPlayerByID(int playerID) {
        return players[playerID];
    }

    /**
     * Nested class
     * This class does the server work to communicate with every client
     */
    public class ClientWorker implements Runnable {
        // Instance variables
        private Socket socket = null;
        private Server server = null;
        private ObjectInputStream inputStream = null;
        private ObjectOutputStream outputStream = null;
        private GameModel model;
        private int playerID;

        /**
         * Constructor
         */
        protected ClientWorker(Socket socket, Server server, int playerID) {
            this.socket = socket;
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
                // Get an object from a client via input stream and pass
                // to the event handler method
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
         * Model setter
         * @param model
         */
        public void setModel(GameModel model) {
            this.model = model;
        }

        /**
         * Event handler method
         * Accepts data and reacts according to the data type (ExchangeEvent)
         * Synchronized to allow one thread at a time
         * @param data
         */
        private synchronized void handleEvent(Data data) {
            ExchangeEvent type = data.getType();

            switch (type) {
                case NEW_MOVE:
                    handleMove(data);
                    break;
                case REQUEST_NEW_GAME:
                    handleRequestNewGame();
                    break;
            }
        }

        /**
         * Send data via parent (only parent have access for both clients)
         * @param targetPlayerID
         * @param data
         */
        protected void dispatcher(int targetPlayerID, Data data) {
            this.server.transmit(targetPlayerID, data);
        }

        /**
         * Send data to the client
         * @param data
         */
        protected void transmitData(Data data) {
            try {
                outputStream.writeObject(data);
                outputStream.reset();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Send corresponding player ID to the client
         */
        public void sendPlayerID() {
            // Create a data object with the required payload
            Data dataToSend = new Data(ExchangeEvent.ASSIGN_PLAYER_ID);
            dataToSend.setPayload(new PlayerData(playerID));

            // Ask the server to send
            dispatcher(playerID, dataToSend);
        }

        /**
         * Send "await for another player" signal to the client
         */
        public void sendAwaitAnotherPlayer() {
            // Create a data object without payload to send only status
            Data dataToSend = new Data(ExchangeEvent.WAITING_FOR_OPPONENT);

            // Ask the server to send
            dispatcher(playerID, dataToSend);
        }

        /**
         * Send new game data to the client
         */
        public void sendNewGame() {
            // Prepare a payload with board state, allowed moves array and the current active player
            NewRoundData newRoundData = new NewRoundData(model.getBoard(), model.generateAllowedMoves(), model.getActivePlayerID());
            Data dataToSend = new Data(ExchangeEvent.NEW_GAME);
            // TODO: use different constructors
            dataToSend.setPayload(newRoundData);

            // Ask the server to send
            dispatcher(playerID, dataToSend);
        }

        /**
         * Send a signal that there is enough players to the client
         */
        public void sendEnoughPlayers() {
            Data dataToSend = new Data(ExchangeEvent.REQUEST_NEW_GAME);

            // Ask the server to send
            dispatcher(playerID, dataToSend);
        }

        // TODO: refactor
        /**
         * Process the NEW_MOVE event
         * Make model adjustments, calculate new allowed moves and change active player
         * Send new data to both players (for them to update the UI)
         * @param receivedData
         */
        private void handleMove(Data receivedData) {
            // If current client worker is not active - don't do nothing
            if (!isActivePlayer()) return;

            int currentPlayerID = model.getActivePlayerID();

            // Make a move in the model
            MoveData move = (MoveData) receivedData.getPayload();
            this.model.performMove(move);

            // If the move is jump - check if there are additional jumps
            // If there are such jumps - generate new allowed moves and allow
            // the player to make a move again
            // If not - just switch the active player
            if (move.isJump()) {
                ArrayList<MoveData> allowedMoves = model.generateAllowedMovesFrom(move.getToRow(), move.getToCol());
                if (allowedMoves == null) {
                    changeActivePlayer();
                }
            } else {
                changeActivePlayer();
            }

            // Prepare the allowed moves array
            ArrayList<MoveData> allowedMoves = model.generateAllowedMoves();

            // If it's empty - game over. Notify both players
            if (allowedMoves == null) {
                sendGameOver(currentPlayerID);
            } else {
                // Prepare the payload
                NewRoundData newRoundData = new NewRoundData(model.getBoard(), allowedMoves, model.getActivePlayerID());
                Data dataToSend = new Data(ExchangeEvent.NEW_ROUND);
                dataToSend.setPayload(newRoundData);

                // Ask the server to send for both players
                for (int i = 0; i < 2; i++) {
                    dispatcher(i, dataToSend);
                }
            }
        }

        /**
         * Change active player to the opposite player
         * (no need to provide ID as we have only two players)
         */
        private void changeActivePlayer() {
            int otherPlayerID = getOtherPlayerID();
            model.setActivePlayerID(otherPlayerID);
        }

        /**
         * Send a game over signal to both clients
         * @param winnerID
         */
        private void sendGameOver(int winnerID) {
            // Prepare the data with the payload (winner player ID)
            Data dataToSend = new Data(ExchangeEvent.GAME_OVER);
            dataToSend.setPayload(new GameOverData(winnerID));

            // Ask the server to send for both players
            for (int i = 0; i < 2; i++) {
                dispatcher(i, dataToSend);
            }

            // As game is over - reset playersReady counter
            this.server.setPlayersReady(0);
        }

        /**
         * Process the REQUEST_NEW_GAME event
         * Ask server to increment the number of "ready" players
         */
        private void handleRequestNewGame() {
            this.server.onPlayerRequestNewGame();
        }

        /**
         * Opposite player's ID getter
         * @return
         */
        private int getOtherPlayerID() {
            return model.getActivePlayerID() == 0 ? 1 : 0;
        }

        /**
         * Check if current player is an active player
         * @return
         */
        private Boolean isActivePlayer() {
            if (playerID == model.getActivePlayerID())
                return true;

            return false;
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
