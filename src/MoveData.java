import java.io.Serializable;

public class MoveData implements Serializable {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public MoveData(int fromRow, int fromCol, int toRow, int toCol, Boolean isInverse) {
        this.fromRow = isInverse ? (7 - fromRow) : fromRow;
        this.fromCol = isInverse ? (7 - fromCol) : fromCol;
        this.toRow = isInverse ? (7 - toRow) : toRow;
        this.toCol = isInverse ? (7 - toCol) : toCol;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getToCol() {
        return toCol;
    }

    public int getToRow() {
        return toRow;
    }

    public boolean isJump() {
        // Test whether this move is a jump. It is assumed that
        // the move is legal. In a jump, the piece moves two
        // rows. (In a regular move, it only moves one row.)
        return (fromRow - toRow == 2 || fromRow - toRow == -2);
    }

    @Override
    public String toString() {
        return "MoveData{" +
                "fromRow=" + fromRow +
                ", fromCol=" + fromCol +
                ", toRow=" + toRow +
                ", toCol=" + toCol +
                '}';
    }
}