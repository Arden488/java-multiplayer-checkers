import java.util.ArrayList;
import java.util.HashMap;

/**
 * Game model class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class is responsible for game data and logic handling
 */

public class GameModel {
    private int[][] board;
    private int activePlayerID = 0;
    private HashMap<int[], ArrayList> allowedMoves;

    // Cell states (empty or checkers)
    public static final int EMPTY = 0;
    public static final int RED = 1;
    public static final int RED_KING = 2;
    public static final int WHITE = 3;
    public static final int WHITE_KING = 4;

    /**
     * Constructor
     */
    public GameModel() {
        // Create new board data
        board = new int[8][8];

        // Set new game state
        resetGame();
    }

    /**
     * Method to reset the game state
     * Iterates over every cell and sets initial checker positions
     * Uses constants to assign numbers from 0 to 4
     */
    private void resetGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == col % 2) {
                    board[row][col] = EMPTY;
                } else {
                    if (row < 3)
                        board[row][col] = WHITE;
                    else if (row > 4)
                        board[row][col] = RED;
                    else
                        board[row][col] = EMPTY;
                }
            }
        }
    }

    private void generateAllowedMoves() {
        ArrayList moves1 = new ArrayList();
        moves1.add(new int[]{4, 1});
        allowedMoves.put(new int[]{5, 0}, moves1);

        ArrayList moves2 = new ArrayList();
        moves2.add(new int[]{4, 1});
        moves2.add(new int[]{4, 3});
        allowedMoves.put(new int[]{5, 2}, moves2);

        ArrayList moves3 = new ArrayList();
        moves3.add(new int[]{4, 3});
        moves3.add(new int[]{4, 5});
        allowedMoves.put(new int[]{5, 4}, moves3);

        ArrayList moves4 = new ArrayList();
        moves4.add(new int[]{4, 7});
        moves4.add(new int[]{4, 5});
        allowedMoves.put(new int[]{5, 6}, moves4);
    }

    public void makeMove(MoveData move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
    }

    /**
     * Board data getter
     * @return int[][] board
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * Get cell state at target row and col
     * @param row
     * @param col
     * @return int piece or empty
     */
    public int getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Active player ID getter
     * @return int activePlayerID
     */
    public int getActivePlayerID() {
        return activePlayerID;
    }

    /**
     * Active player ID setter
     * @param playerID
     */
    public void setActivePlayerID(int playerID) {
        this.activePlayerID = playerID;
    }

    /**
     * Allowed moves getter
     * @return
     */
    public HashMap<int[], ArrayList> getAllowedMoves() {
        return allowedMoves;
    }
}
