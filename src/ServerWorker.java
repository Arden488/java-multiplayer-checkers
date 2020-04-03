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

public class ServerWorker implements Runnable, GameStatus {
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
            System.out.println("Send new game data");
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

        // TODO: use common class to avoid repetition
        switch (type) {
            case "MOVE":
                handleMove(data);
            case "REQUEST_NEW_GAME":
                handleRequestNewGame();
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
        NewRoundData newRoundData = new NewRoundData(model.getBoard(), model.getAllowedMoves(), model.getActivePlayerID());
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

        // TODO: DO SOMETHING WITH RECEIVED DATA
        MoveData move = (MoveData) receivedData.getPayload();
        this.model.makeMove(move);

        int otherPlayerID = getOtherPlayerID();
        model.setActivePlayerID(otherPlayerID);

        NewRoundData newRoundData = new NewRoundData(model.getBoard(), model.getAllowedMoves(), model.getActivePlayerID());
        Data dataToSend = new Data("NEW_ROUND");
        dataToSend.setPayload(newRoundData);

        dispatcher(0, dataToSend);
        dispatcher(1, dataToSend);
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
