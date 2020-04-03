/**
 * Move data class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class carries data about the move "from" and "to" position
 */

import java.io.Serializable;

public class MoveData implements Serializable {
    // Instance variables
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    /**
     * Constructor
     * @param fromRow
     * @param fromCol
     * @param toRow
     * @param toCol
     * @param isInverse
     */
    public MoveData(int fromRow, int fromCol, int toRow, int toCol, Boolean isInverse) {
        this.fromRow = isInverse ? (7 - fromRow) : fromRow;
        this.fromCol = isInverse ? (7 - fromCol) : fromCol;
        this.toRow = isInverse ? (7 - toRow) : toRow;
        this.toCol = isInverse ? (7 - toCol) : toCol;
    }

    /**
     * From column getter
     * @return
     */
    public int getFromCol() {
        return fromCol;
    }

    /**
     * From row getter
     * @return
     */
    public int getFromRow() {
        return fromRow;
    }

    /**
     * To column getter
     * @return
     */
    public int getToCol() {
        return toCol;
    }

    /**
     * To row getter
     * @return
     */
    public int getToRow() {
        return toRow;
    }

    /**
     * Check if the allowed move is a jmp
     * @return
     */
    public boolean isJump() {
        return (fromRow - toRow == 2 || fromRow - toRow == -2);
    }
}