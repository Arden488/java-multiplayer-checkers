import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NewRoundData implements Serializable {
    private int[][] boardState;
    private ArrayList<MoveData> allowedMoves;
    private int activePlayerID;

    public NewRoundData(int[][] boardState, ArrayList<MoveData> allowedMoves, int activePlayerID) {
        this.boardState = boardState;
        this.allowedMoves = allowedMoves;
        this.activePlayerID = activePlayerID;
    }

    public int[][] getBoardState() {
        return boardState;
    }

    public ArrayList<MoveData> getAllowedMoves() {
        return allowedMoves;
    }

    public int getActivePlayerID() {
        return activePlayerID;
    }

    // TODO: remove
    @Override
    public String toString() {
        return "NewRoundData{" +
                "boardState=" + Arrays.toString(boardState) +
                ", allowedMoves=" + allowedMoves +
                ", activePlayerID=" + activePlayerID +
                '}';
    }
}