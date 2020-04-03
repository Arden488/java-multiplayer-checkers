/**
 * New round data class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class carries data about the new round
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class NewRoundData implements Serializable {
    // Instance variables
    private int[][] boardState;
    private ArrayList<MoveData> allowedMoves;
    private int activePlayerID;

    /**
     * Constructor
     * @param boardState
     * @param allowedMoves
     * @param activePlayerID
     */
    public NewRoundData(int[][] boardState, ArrayList<MoveData> allowedMoves, int activePlayerID) {
        this.boardState = boardState;
        this.allowedMoves = allowedMoves;
        this.activePlayerID = activePlayerID;
    }

    /**
     * Board state getter
     * @return
     */
    public int[][] getBoardState() {
        return boardState;
    }

    /**
     * Allowed moves getter
     * @return
     */
    public ArrayList<MoveData> getAllowedMoves() {
        return allowedMoves;
    }

    /**
     * Active player ID getter
     * @return
     */
    public int getActivePlayerID() {
        return activePlayerID;
    }
}