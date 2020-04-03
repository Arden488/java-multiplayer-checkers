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

public class Server implements Runnable {
    // Port to connect
    private final int PORT = 5678;
    private final int REQUIRED_PLAYERS = 2;
    private int connectionNum = 0;
    private ServerSocket server;

    private GameModel model;
    // TODO: reset after player disconnect
    private int playersReady = 0;
    private ClientWorker players[] = new ClientWorker[2];

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

                // If connections is less than acceptable limit (2)
                // connect and create a client
                // else - close the socket
                if (connectionNum < REQUIRED_PLAYERS) {
                    connectionNum++;

                    // Identify vacant player slots (0 or 1)
                    int playerID = players[0] == null ? 0 : 1;

                    // Create a client worker
                    ClientWorker client = new ClientWorker(clientSocket, this, playerID);
                    // Add client to the players array
                    players[playerID] = client;

                    // Create a client thread
                    new Thread(client).start();

                    client.sendPlayerID();
                    client.handleAwaitAnotherPlayer();

                    if (connectionNum == REQUIRED_PLAYERS) {
                        allowNewGame();
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

    private void allowNewGame() {
        players[0].handleEnoughPlayers();
        players[1].handleEnoughPlayers();
    }

    protected void transmit(int targetPlayerID, Data data) {
        ClientWorker targetPlayer = getPlayerByID(targetPlayerID);

        targetPlayer.transmitData(data);
    }

    /**
     * Remove the player from the players list on disconnect
     * @param playerID
     */
    protected void onPlayerDisconnect(int playerID) {
        players[playerID] = null;
        connectionNum--;
        playersReady--;
    }

    public void onPlayerRequestNewGame() {
        playersReady++;

        if (playersReady == REQUIRED_PLAYERS) {
            // Initiate new game model
            model = new GameModel();

            for (int i = 0; i < 2; i++) {
                players[i].setModel(model);
                players[i].handleNewGame();
            }
        }

    }

    public void setPlayersReady(int playersReady) {
        this.playersReady = playersReady;
    }

    private ClientWorker getPlayerByID(int playerID) {
        return players[playerID];
    }

    /**
     * Inner class
     * This class does the server work to communicate with every client
     */
    public class ClientWorker implements Runnable {
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

        public void setModel(GameModel model) {
            this.model = model;
        }

        /**
         * Event handler method
         * Accepts data and reacts according to the data type
         * @param data
         */
        private void handleEvent(Data data) {
            String type = data.getType();

            // TODO: use common class to avoid repetition
            switch (type) {
                case "MOVE":
                    handleMove(data);
                    break;
                case "REQUEST_NEW_GAME":
                    handleRequestNewGame();
                    break;
            }
        }

        protected void dispatcher(int targetPlayerID, Data data) {
            // TODO: use common class to avoid repetition
            this.server.transmit(targetPlayerID, data);
        }

        protected void transmitData(Data data) {
            try {
                outputStream.writeObject(data);
                outputStream.reset();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        public void sendPlayerID() {
            Data dataToSend = new Data("ASSIGN_PLAYER_ID");
            dataToSend.setPayload(new PlayerData(playerID));

            dispatcher(playerID, dataToSend);
        }

        public void handleAwaitAnotherPlayer() {
            Data dataToSend = new Data("WAITING_FOR_OPPONENT");

            // TODO: send both players method
            dispatcher(playerID, dataToSend);
        }

        public void handleNewGame() {
            NewRoundData newRoundData = new NewRoundData(model.getBoard(), model.generateAllowedMoves(), model.getActivePlayerID());
            Data dataToSend = new Data("NEW_GAME");
            // TODO: use different constructors
            dataToSend.setPayload(newRoundData);

            dispatcher(playerID, dataToSend);
        }

        public void handleEnoughPlayers() {
            Data dataToSend = new Data("REQUEST_NEWGAME");

            dispatcher(playerID, dataToSend);
        }

        private void handleMove(Data receivedData) {
            if (!isActivePlayer()) return;

            int currentPlayerID = model.getActivePlayerID();

            // TODO: DO SOMETHING WITH RECEIVED DATA
            MoveData move = (MoveData) receivedData.getPayload();
            this.model.makeMove(move);

            // TODO: refactor
            if (move.isJump()) {
                ArrayList<MoveData> allowedMoves = model.generateAllowedMovesFrom(move.getToRow(), move.getToCol());
                if (allowedMoves == null) {
                    changeActivePlayer();
                }
            } else {
                changeActivePlayer();
            }

            ArrayList<MoveData> allowedMoves = model.generateAllowedMoves();
            if (allowedMoves == null) {
                handleGameOver(currentPlayerID);
            } else {
                NewRoundData newRoundData = new NewRoundData(model.getBoard(), allowedMoves, model.getActivePlayerID());
                Data dataToSend = new Data("NEW_ROUND");
                dataToSend.setPayload(newRoundData);

                for (int i = 0; i < 2; i++) {
                    dispatcher(i, dataToSend);
                }
            }
        }

        private void changeActivePlayer() {
            int otherPlayerID = getOtherPlayerID();
            model.setActivePlayerID(otherPlayerID);
        }

        private void handleGameOver(int winnerID) {
            Data dataToSend = new Data("GAME_OVER");
            dataToSend.setPayload(new GameOverData(winnerID));

            for (int i = 0; i < 2; i++) {
                dispatcher(i, dataToSend);
            }

            this.server.setPlayersReady(0);
        }

        private void handleRequestNewGame() {
            this.server.onPlayerRequestNewGame();
        }

        private int getOtherPlayerID() {
            return model.getActivePlayerID() == 0 ? 1 : 0;
        }

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
