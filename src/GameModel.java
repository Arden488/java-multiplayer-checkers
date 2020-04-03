/**
 * Game model class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class is responsible for game data and logic handling
 */

import java.util.ArrayList;

public class GameModel implements GameSettings {
    private int[][] board;
    private int activePlayerID = 1;

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
    public void resetGame() {
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

        setActivePlayerID(0);
        generateAllowedMoves();
    }

    // TODO: refactor
    public ArrayList<MoveData> generateAllowedMoves() {
        int player = activePlayerID;

        if (player != RED && player != WHITE)
            return null;

        int playerKing; // The constant representing a King belonging to player.
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = WHITE_KING;

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        // Return an array containing all the legal CheckersMoves
        // for the specfied player on the current board. If the player
        // has no legal moves, null is returned. The value of player
        // should be one of the constants RED or BLACK; if not, null
        // is returned. If the returned value is non-null, it consists
        // entirely of jump moves or entirely of regular moves, since
        // if the player can jump, only jumps are legal moves.
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player || board[row][col] == playerKing) {
                    if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2))
                        allowedMoves.add(new MoveData(row, col, row + 2,col + 2, !isPlayingRed()));
                    if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2))
                        allowedMoves.add(new MoveData(row, col, row - 2, col + 2, !isPlayingRed()));
                    if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2))
                        allowedMoves.add(new MoveData(row, col, row + 2, col - 2, !isPlayingRed()));
                    if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2))
                        allowedMoves.add(new MoveData(row, col, row - 2, col - 2, !isPlayingRed()));
                }
            }
        }

        /*
         * First, check for any possible jumps. Look at each square on the
         * board. If that square contains one of the player's pieces, look at a
         * possible jump in each of the four directions from that square. If
         * there is a legal jump in that direction, put it in the moves vector.
         */
        if (allowedMoves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canMove(player, row, col, row + 1, col + 1))
                            allowedMoves.add(new MoveData(row, col, row + 1, col + 1, !isPlayingRed()));
                        if (canMove(player, row, col, row - 1, col + 1))
                            allowedMoves.add(new MoveData(row, col, row - 1, col + 1, !isPlayingRed()));
                        if (canMove(player, row, col, row + 1, col - 1))
                            allowedMoves.add(new MoveData(row, col, row + 1, col - 1, !isPlayingRed()));
                        if (canMove(player, row, col, row - 1, col - 1))
                            allowedMoves.add(new MoveData(row, col, row - 1, col - 1, !isPlayingRed()));
                    }
                }
            }
        }

        if (allowedMoves.size() == 0)
            return null;
        else {
            return allowedMoves;
        }
    }

    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3,
                            int c3) {
        // This is called by the two previous methods to check whether the
        // player can legally jump from (r1,c1) to (r3,c3). It is assumed
        // that the player has a piece at (r1,c1), that (r3,c3) is a position
        // that is 2 rows and 2 columns distant from (r1,c1) and that
        // (r2,c2) is the square between (r1,c1) and (r3,c3).

        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false; // (r3,c3) is off the board.

        if (board[r3][c3] != EMPTY)
            return false; // (r3,c3) already contains a piece.

        if (player == RED) {
            if (board[r1][c1] == RED && r3 > r1)
                return false; // Regular red piece can only move up.
            if (board[r2][c2] != WHITE && board[r2][c2] != WHITE_KING)
                return false; // There is no black piece to jump.
            return true; // The jump is legal.
        } else {
            if (board[r1][c1] == WHITE && r3 < r1)
                return false; // Regular black piece can only move downn.
            if (board[r2][c2] != RED && board[r2][c2] != RED_KING)
                return false; // There is no red piece to jump.
            return true; // The jump is legal.
        }

    } // end canJump()

    private boolean canMove(int player, int r1, int c1, int r2, int c2) {
        // This is called by the getLegalMoves() method to determine whether
        // the player can legally move from (r1,c1) to (r2,c2). It is
        // assumed that (r1,r2) contains one of the player's pieces and
        // that (r2,c2) is a neighboring square.

        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false; // (r2,c2) is off the board.

        if (board[r2][c2] != EMPTY)
            return false; // (r2,c2) already contains a piece.

        if (player == RED) {
            if (board[r1][c1] == RED && r2 > r1)
                return false; // Regular red piece can only move down.
            return true; // The move is legal.
        } else {
            if (board[r1][c1] == WHITE && r2 < r1)
                return false; // Regular black piece can only move up.
            return true; // The move is legal.
        }

    } // end canMove()

    public ArrayList<MoveData> generateAllowedMovesFrom(int row, int col) {
        int player = activePlayerID;

        // Return a list of the legal jumps that the specified player can
        // make starting from the specified row and column. If no such
        // jumps are possible, null is returned. The logic is similar
        // to the logic of the getLegalMoves() method.
        if (player != RED && player != WHITE)
            return null;
        int playerKing; // The constant representing a King belonging to player.
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = WHITE;

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        // vector.
        if (board[row][col] == player || board[row][col] == playerKing) {
            if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2))
                allowedMoves.add(new MoveData(row, col, row + 2, col + 2, !isPlayingRed()));
            if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2))
                allowedMoves.add(new MoveData(row, col, row - 2, col + 2, !isPlayingRed()));
            if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2))
                allowedMoves.add(new MoveData(row, col, row + 2, col - 2, !isPlayingRed()));
            if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2))
                allowedMoves.add(new MoveData(row, col, row - 2, col - 2, !isPlayingRed()));
        }
        if (allowedMoves.size() == 0)
            return null;
        else {
            return allowedMoves;
        }
    } // end getLegalMovesFrom()

    public void makeMove(MoveData move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

//        board[toRow][toCol] = board[fromRow][fromCol];
//        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            // The move is a jump. Remove the jumped piece from the board.
            int jumpRow = (fromRow + toRow) / 2; // Row of the jumped piece.
            int jumpCol = (fromCol + toCol) / 2; // Column of the jumped piece.
            board[jumpRow][jumpCol] = EMPTY;
        }
        if (toRow == 0 && board[toRow][toCol] == RED)
            board[toRow][toCol] = RED_KING;
        if (toRow == 7 && board[toRow][toCol] == WHITE)
            board[toRow][toCol] = WHITE_KING;
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
        return activePlayerID == RED ? 0 : 1;
    }

    /**
     * Active player ID setter
     * @param playerID
     */
    public void setActivePlayerID(int playerID) {
        this.activePlayerID = playerID == 0 ? RED : WHITE;
    }

    private Boolean isPlayingRed() {
        return this.activePlayerID == RED;
    }
}
