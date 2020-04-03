/**
 * Game model class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class is responsible for game data and logic handling
 */

import java.util.ArrayList;

public class GameModel implements GameSettings {
    // Instance variables
    private int[][] board;
    private int activePlayerID = 0;
    private int playerPieceID = RED;
    private int playerKingID = RED_KING;

    /**
     * Constructor
     */
    public GameModel() {
        // Create new board data
        board = new int[8][8];

        // Set new game state
        newGame();
    }

    /**
     * Method to start the new game state
     * Iterates over every cell and sets initial checker positions
     */
    public void newGame() {
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
     * Generate the array of the allowed moves
     * Every move is an instance of MoveData object - it store "from" and "to" coordinates
     * @return
     */
    public ArrayList<MoveData> generateAllowedMoves() {
        int pieceID = getPlayerPieceID();

        // If there is a wrong ID - do not proceed
        if (pieceID != RED && pieceID != WHITE)
            return null;

        // Set the player king variable to check the cell against
        int kingID = getPlayerKingID();

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        // Check if there are any jumps available
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == pieceID || board[row][col] == kingID) {
                    allowedMoves.addAll(checkPossibleJumps(row, col));
                }
            }
        }

        // If there are no jumps - calculate simple moves
        if (allowedMoves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == pieceID || board[row][col] == kingID) {
                        allowedMoves.addAll(checkPossibleMoves(row, col));
                    }
                }
            }
        }

        // If not moves or jumps available - return null
        // Else - return the array
        if (allowedMoves.size() == 0)
            return null;
        else {
            return allowedMoves;
        }
    }

    /**
     * Generate allowed jumps only
     * @param row
     * @param col
     * @return
     */
    public ArrayList<MoveData> generateAllowedMovesFrom(int row, int col) {
        int pieceID = getPlayerPieceID();

        // If there is a wrong ID - do not proceed
        if (pieceID != RED && pieceID != WHITE)
            return null;

        // Set the player king variable to check the cell against
        int kingID = getPlayerKingID();

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        // Check only possible jumps from the specific position
        if (board[row][col] == pieceID || board[row][col] == kingID) {
            allowedMoves = checkPossibleJumps(row, col);
        }

        // If not moves or jumps available - return null
        // Else - return the array
        if (allowedMoves.size() == 0)
            return null;
        else {
            return allowedMoves;
        }
    }

    /**
     * Check any possible jumps to every direction for the specific coordinate
     * @param row
     * @param col
     * @return
     */
    private ArrayList<MoveData> checkPossibleJumps(int row, int col) {
        int pieceID = getPlayerPieceID();

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        if (checkIfJumpAllowed(pieceID, row, col, row + 1, col + 1, row + 2, col + 2)) {
            allowedMoves.add(new MoveData(row, col, row + 2, col + 2, !isPlayingRed()));
        }

        if (checkIfJumpAllowed(pieceID, row, col, row - 1, col + 1, row - 2, col + 2)) {
            allowedMoves.add(new MoveData(row, col, row - 2, col + 2, !isPlayingRed()));
        }

        if (checkIfJumpAllowed(pieceID, row, col, row + 1, col - 1, row + 2, col - 2)) {
            allowedMoves.add(new MoveData(row, col, row + 2, col - 2, !isPlayingRed()));
        }

        if (checkIfJumpAllowed(pieceID, row, col, row - 1, col - 1, row - 2, col - 2)) {
            allowedMoves.add(new MoveData(row, col, row - 2, col - 2, !isPlayingRed()));
        }

        return allowedMoves;
    }

    /**
     * Check any possible moves to every direction for the specific coordinate
     * @param row
     * @param col
     * @return
     */
    private ArrayList<MoveData> checkPossibleMoves(int row, int col) {
        int pieceID = getPlayerPieceID();

        ArrayList<MoveData> allowedMoves = new ArrayList<MoveData>();

        if (checkIfMoveAllowed(pieceID, row, col, row + 1, col + 1)) {
            allowedMoves.add(new MoveData(row, col, row + 1, col + 1, !isPlayingRed()));
        }

        if (checkIfMoveAllowed(pieceID, row, col, row - 1, col + 1)) {
            allowedMoves.add(new MoveData(row, col, row - 1, col + 1, !isPlayingRed()));
        }

        if (checkIfMoveAllowed(pieceID, row, col, row + 1, col - 1)) {
            allowedMoves.add(new MoveData(row, col, row + 1, col - 1, !isPlayingRed()));
        }

        if (checkIfMoveAllowed(pieceID, row, col, row - 1, col - 1)) {
            allowedMoves.add(new MoveData(row, col, row - 1, col - 1, !isPlayingRed()));
        }

        return allowedMoves;
    }

    /**
     * Check if there is a jump by comparing three cell positions
     * @param pieceID
     * @param r1
     * @param c1
     * @param r2
     * @param c2
     * @param r3
     * @param c3
     * @return
     */
    private boolean checkIfJumpAllowed(int pieceID, int r1, int c1, int r2, int c2, int r3, int c3) {
        // If not within boundaries - exit
        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false;

        // If jump target is not empty - it is blocked. exit
        if (board[r3][c3] != EMPTY)
            return false;

        // Check if piece can move (only forward allowed) and if there is an opponent
        // next to it
        if (pieceID == RED) {
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

    /**
     * Check if there is a move by comparing two cell positions
     * @param pieceID
     * @param r1
     * @param c1
     * @param r2
     * @param c2
     * @return
     */
    private boolean checkIfMoveAllowed(int pieceID, int r1, int c1, int r2, int c2) {
        // If not within boundaries - exit
        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false;

        // If jump target is not empty - it is blocked. exit
        if (board[r2][c2] != EMPTY)
            return false;

        // Check if piece can move (only forward allowed)
        if (pieceID == RED) {
            if (board[r1][c1] == RED && r2 > r1)
                return false;
            return true;
        } else {
            if (board[r1][c1] == WHITE && r2 < r1)
                return false;
            return true;
        }

    }

    /**
     * Make a move by setting the state for "from" and "to" cells
     * @param move
     */
    public void performMove(MoveData move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        // Switch cell states
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        // If is a jump - remove the defeated piece
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
            board[jumpRow][jumpCol] = EMPTY;
        }

        // Make a king from the piece
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
        setPlayerPieceID(playerID == 0 ? RED : WHITE);
    }

    /**
     * Player piece ID getter
     * @return
     */
    public int getPlayerPieceID() {
        return playerPieceID;
    }

    /**
     * Player king ID getter
     * @return
     */
    public int getPlayerKingID() {
        return playerKingID;
    }

    /**
     * Player piece and king ID setter
     * @param playerPieceID
     */
    public void setPlayerPieceID(int playerPieceID) {
        this.playerPieceID = playerPieceID;
        this.playerKingID = playerPieceID == RED ? RED_KING : WHITE_KING;
    }

    /**
     * Check if an active player is RED
     * @return
     */
    private Boolean isPlayingRed() {
        return this.playerPieceID == RED;
    }
}
