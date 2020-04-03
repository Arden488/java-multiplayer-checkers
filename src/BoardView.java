/**
 * Board view class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class is responsible for the board graphics
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class BoardView extends JPanel implements MouseListener, ViewSettings, GameSettings {
    // Instance variables
    private Client.Worker worker = null;
    private Boolean gameInProgress = false;
    private Boolean gameOver = false;
    private int winnerID;
    private int[][] boardData;
    private ArrayList<MoveData> allowedMoves;
    private int selectedRow = -1;
    private int selectedCol = -1;

    /**
     * Constructor
     */
    public BoardView(Client.Worker worker) {
        this.worker = worker;
        this.addMouseListener(this);
    }

    /**
     * Method to paint the board
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        // Set graphics settings
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // For every cell either draw a piece or skip
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                this.drawBoardCell(g, row, col);
            }
        }

        /**
         * Highlight the selected row and column
         */
        if (selectedRow >= 0 && selectedCol >= 0) {
            int cellXPos = selectedCol * CELL_SIZE + BOARD_BORDER_WIDTH;
            int cellYPos = selectedRow * CELL_SIZE + BOARD_BORDER_WIDTH;

            g.setColor(SELECTED_COLOR);
            g.fillRect(cellXPos, cellYPos, CELL_SIZE, CELL_SIZE);
        }

        // Draw a game over state
        if (gameOver) {
            drawGameOver(g);
        }
    }

    /**
     * Board data setter
     * @param board
     */
    public void setBoardData(int[][] board) {
        this.boardData = board;
    }

    /**
     * Allowed moves setter
     * @param allowedMoves
     */
    public void setAllowedMoves(ArrayList<MoveData> allowedMoves) {
        this.allowedMoves = allowedMoves;
    }

    /**
     * Repaint the board
     */
    public void updateBoard() {
        repaint();
    }

    /**
     * Draw either a piece or an empty board cell
     * @param g
     * @param row
     * @param col
     */
    private void drawBoardCell(Graphics g, int row, int col) {
        // Draw cells chequerwise
        if (row % 2 == col % 2)
            g.setColor(CELL_ODD_COLOR);
        else
            g.setColor(CELL_EVEN_COLOR);

        int cellXPos = col * CELL_SIZE + BOARD_BORDER_WIDTH;
        int cellYPos = row * CELL_SIZE + BOARD_BORDER_WIDTH;
        g.fillRect(cellXPos, cellYPos, CELL_SIZE, CELL_SIZE);

        // Do not proceed to draw pieces on the initial client load
        // (a game is neither started nor over)
        if (!gameInProgress && !gameOver)
            return;

        // Calculate possible piece position and styles
        int cellPieceMargin = 10;
        int shadowSize = 4;
        int borderWidth = 1;
        int pieceXPos = cellXPos + (cellPieceMargin / 2);
        int pieceYPos = cellYPos + (cellPieceMargin / 2) - (shadowSize / 2);
        int pieceWidth = CELL_SIZE - cellPieceMargin;
        int pieceHeight = CELL_SIZE - cellPieceMargin;

        // Get the cell state (piece or empty)
        int piece = getBoardPiece(row, col);

        // Prepare styles for the "King" label
        Font kingLabelFont = new Font("Arial", Font.PLAIN, 12);
        FontMetrics metrics = g.getFontMetrics(kingLabelFont);
        String kingLabelText = "King";
        int kingLabelPosX = pieceXPos + (CELL_SIZE / 2) - (metrics.stringWidth(kingLabelText) / 2) - 5;
        int kingLabelPosY = pieceYPos + (CELL_SIZE / 2);

        // Draw pieces
        switch (piece) {
            case RED:
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(RED_PIECE_COLOR);
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                break;
            case WHITE:
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(WHITE_PIECE_COLOR);
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                break;
            case RED_KING:
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(RED_PIECE_COLOR);
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                g.setColor(WHITE_PIECE_COLOR);
                g.setFont(kingLabelFont);
                g.drawString(kingLabelText, kingLabelPosX, kingLabelPosY);
                break;
            case WHITE_KING:
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(PIECE_SHADOW_COLOR);
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(WHITE_PIECE_COLOR);
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                g.setColor(TEXT_COLOR);
                g.drawString(kingLabelText, kingLabelPosX, kingLabelPosY);
                break;
        }
    }

    /**
     * Draw a game over state (two strings)
     * @param g
     */
    private void drawGameOver(Graphics g) {
        String text1 = "GAME OVER";
        String text2 = "WINNER: " + (winnerID == 0 ? "RED" : "WHITE");
        g.setColor(TEXT_COLOR);
        Font font = new Font("Arial", Font.BOLD, 40);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        int canvasWidth = (CELL_SIZE * 8) + (BOARD_BORDER_WIDTH * 2);
        int canvasHeight = (CELL_SIZE * 8) + (BOARD_BORDER_WIDTH * 2);
        int gameOverPosX = (canvasWidth - metrics.stringWidth(text1)) / 2;
        int gameOverPosY = ((canvasHeight - metrics.getHeight()) / 2) + metrics.getAscent() - (metrics.getHeight() / 2);
        int winnerPosX = (canvasWidth - metrics.stringWidth(text2)) / 2;
        int winnerPosY = ((canvasHeight - metrics.getHeight()) / 2) + metrics.getAscent() + (metrics.getHeight() / 2);
        g.drawString(text1, gameOverPosX, gameOverPosY);
        g.drawString(text2, winnerPosX, winnerPosY);
    }

    /**
     * Set game over and game in progress states
     * @param winnerID
     */
    public void displayWinner(int winnerID) {
        setGameInProgress(false);
        setGameOver(true, winnerID);
    }

    /**
     * Get the proper board cell state (empty or piece)
     * Takes into account the "mirrored" state of the board fot
     * the second player
     * @param row
     * @param col
     * @return
     */
    private int getBoardPiece(int row, int col) {
        int tRow = this.worker.isPlayingRed() ? row : (7 - row);
        int tCol = this.worker.isPlayingRed() ? col : (7 - col);
        return boardData[tRow][tCol];
    }

    /**
     * Set game over states
     * @param status
     * @param winnerID
     */
    public void setGameOver(Boolean status, int winnerID) {
        this.gameOver = status;
        this.winnerID = winnerID;
    }

    /**
     * Set game in progress state
     * @param gameInProgress
     */
    public void setGameInProgress(Boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    /**
     * Process the click on the board
     * @param row
     * @param col
     */
    public void handleClick(int row, int col) {
        // If the game is not yet started or the active player is not the current player - exit
        if (!gameInProgress || !worker.getIsYourTurn())
            return;

        // Select the cell only for the allowed moves
        for (MoveData move: allowedMoves) {
            if (move.getFromRow() == row && move.getFromCol() == col) {
                selectedRow = row;
                selectedCol = col;

                repaint();
                return;
            }
        }

        // Register the move only if the selected cell and the destination
        // are in the allowed moves array
        for (MoveData move: allowedMoves) {
            if (move.getFromRow() == selectedRow
                    && move.getFromCol() == selectedCol
                    && move.getToRow() == row && move.getToCol() == col) {
                this.worker.registerMove(selectedRow, selectedCol, row, col);

                // Reset selected cell state
                selectedRow = -1;
                selectedCol = -1;

                repaint();
            }
        }
    }

    /**
     * Mouse pressed event
     * Pass the cell coordinates to the click handler
     * @param evt
     */
    public void mousePressed(MouseEvent evt) {
        int col = (evt.getX() - BOARD_BORDER_WIDTH) / CELL_SIZE;
        int row = (evt.getY() - BOARD_BORDER_WIDTH) / CELL_SIZE;
        if (col >= 0 && col < 8 && row >= 0 && row < 8)
            handleClick(row, col);
    }

    /**
     * Mouse released event
     * @param evt
     */
    public void mouseReleased(MouseEvent evt) {}

    /**
     * Mouse entered event
     * @param evt
     */
    public void mouseEntered(MouseEvent evt) {}

    /**
     * Mouse exited event
     * @param evt
     */
    public void mouseExited(MouseEvent evt) {}

    /**
     * Mouse clicked event
     * @param evt
     */
    public void mouseClicked(MouseEvent evt) {}
}
