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

    public ArrayList<MoveData> generateAllowedMoves() {
        int player = activePlayerID;

        if (player != RED && player != WHITE)
            return null;

        int playerKing;
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = WHITE_KING;

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player || board[row][col] == playerKing) {
                    allowedMoves.addAll(checkPossibleJumps(row, col));
                }
            }
        }

        if (allowedMoves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        allowedMoves.addAll(checkPossibleMoves(row, col));
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

    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false;

        if (board[r3][c3] != EMPTY)
            return false;

        if (player == RED) {
            if (board[r1][c1] == RED && r3 > r1)
                return false;
            if (board[r2][c2] != WHITE && board[r2][c2] != WHITE_KING)
                return false;
            return true;
        } else {
            if (board[r1][c1] == WHITE && r3 < r1)
                return false;
            if (board[r2][c2] != RED && board[r2][c2] != RED_KING)
                return false;
            return true;
        }

    }

    private boolean canMove(int player, int r1, int c1, int r2, int c2) {
        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false;

        if (board[r2][c2] != EMPTY)
            return false;

        if (player == RED) {
            if (board[r1][c1] == RED && r2 > r1)
                return false;
            return true;
        } else {
            if (board[r1][c1] == WHITE && r2 < r1)
                return false;
            return true;
        }

    }

    public ArrayList<MoveData> generateAllowedMovesFrom(int row, int col) {
        int player = activePlayerID;

        if (player != RED && player != WHITE)
            return null;
        int playerKing;
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = WHITE;

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        if (board[row][col] == player || board[row][col] == playerKing) {
            allowedMoves = checkPossibleJumps(row, col);
        }
        if (allowedMoves.size() == 0)
            return null;
        else {
            return allowedMoves;
        }
    }

    private ArrayList<MoveData> checkPossibleJumps(int row, int col) {
        int player = activePlayerID;

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2))
            allowedMoves.add(new MoveData(row, col, row + 2, col + 2, !isPlayingRed()));
        if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2))
            allowedMoves.add(new MoveData(row, col, row - 2, col + 2, !isPlayingRed()));
        if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2))
            allowedMoves.add(new MoveData(row, col, row + 2, col - 2, !isPlayingRed()));
        if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2))
            allowedMoves.add(new MoveData(row, col, row - 2, col - 2, !isPlayingRed()));

        return allowedMoves;
    }

    private ArrayList<MoveData> checkPossibleMoves(int row, int col) {
        int player = activePlayerID;

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        if (canMove(player, row, col, row + 1, col + 1))
            allowedMoves.add(new MoveData(row, col, row + 1, col + 1, !isPlayingRed()));
        if (canMove(player, row, col, row - 1, col + 1))
            allowedMoves.add(new MoveData(row, col, row - 1, col + 1, !isPlayingRed()));
        if (canMove(player, row, col, row + 1, col - 1))
            allowedMoves.add(new MoveData(row, col, row + 1, col - 1, !isPlayingRed()));
        if (canMove(player, row, col, row - 1, col - 1))
            allowedMoves.add(new MoveData(row, col, row - 1, col - 1, !isPlayingRed()));

        return allowedMoves;
    }

    public void makeMove(MoveData move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
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
