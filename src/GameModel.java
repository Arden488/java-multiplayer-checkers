/**
 * Game model class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class is responsible for game data and logic handling
 */

public class GameModel {
    private int[][] board;

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

    /**
     * Board data getter
     * @return int[][] board
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * Get cell state at target row and col
     * @param int row
     * @param int col
     * @return int piece or empty
     */
    public int getPiece(int row, int col) {
        return board[row][col];
    }
}
