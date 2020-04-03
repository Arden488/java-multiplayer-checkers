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
    private int playerID;
    private Boolean isYourTurn;

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
        MoveData move = isPlayingRed() ?
                new MoveData(fromRow, fromCol, toRow, toCol) :
                new MoveData((7 - fromRow), (7 - fromCol), (7 - toRow), (7 - toCol));

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
