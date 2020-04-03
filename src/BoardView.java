/**
 * Board view class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class draws the board
 */

// TODO: import only required
import com.sun.security.ntlm.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

public class BoardView extends JPanel implements MouseListener {
    private ClientWorker worker = null;
    private GameView view = null;
    private Boolean gameInProgress = false;
    private int[][] boardData;

    // TODO: improve DRY
    // Cell states (empty or checkers)
    public static final int EMPTY = 0;
    public static final int RED = 1;
    public static final int RED_KING = 2;
    public static final int WHITE = 3;
    public static final int WHITE_KING = 4;

    private int cellSize;
    private int boardBorderWidth;
    private int windowPanelOffset;
    private HashMap<String, Color> colorMap = new HashMap<String, Color>();

    private int selectedRow = -1;
    private int selectedCol = -1;

    /**
     * Constructor
     */
    public BoardView(ClientWorker worker, GameView view) {
        this.view = view;
        this.worker = worker;
        this.addMouseListener(this);
        this.getViewSettings();
    }

    private void getViewSettings() {
        // TODO: improve DRY
        this.cellSize = view.getCellSize();
        this.boardBorderWidth = view.getBoardBorderWidth();
        this.colorMap = view.getColorMap();
        this.windowPanelOffset = view.getWindowPanelOffset();
    }

    /**
     * Method to paint the board
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // TODO: get current player id
                this.drawBoardCell(g, row, col, this.worker.isPlayingRed());
            }
        }

        if (selectedRow >= 0 && selectedCol >= 0) {
            int cellXPos = selectedCol * cellSize + boardBorderWidth;
            int cellYPos = selectedRow * cellSize + boardBorderWidth;

            g.setColor(colorMap.get("SELECTED"));
            g.fillRect(cellXPos, cellYPos, cellSize, cellSize);
        }
    }

    public void setBoardData(int[][] board) {
        this.boardData = board;
    }

    public void updateBoard() {
        repaint();
    }

    private void drawBoardCell(Graphics g, int row, int col, Boolean isPlayingRed) {
        if (row % 2 == col % 2)
            g.setColor(colorMap.get("CELL_ODD"));
        else
            g.setColor(colorMap.get("CELL_EVEN"));

        int cellXPos = col * cellSize + boardBorderWidth;
        int cellYPos = row * cellSize + boardBorderWidth;
        g.fillRect(cellXPos, cellYPos, cellSize, cellSize);

        int cellPieceMargin = 10;
        int shadowSize = 4;
        int borderWidth = 1;
        int pieceXPos = cellXPos + (cellPieceMargin / 2);
        int pieceYPos = cellYPos + (cellPieceMargin / 2) - (shadowSize / 2);
        int pieceWidth = cellSize - cellPieceMargin;
        int pieceHeight = cellSize - cellPieceMargin;

        if (!gameInProgress)
            return;

        int piece = getBoardPiece(row, col);

        switch (piece) {
            case RED:
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(colorMap.get("RED_PIECE"));
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                break;
            case WHITE:
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(colorMap.get("WHITE_PIECE"));
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                break;
        }
    }

    private int getBoardPiece(int row, int col) {
        int tRow = this.worker.isPlayingRed() ? row : (7 - row);
        int tCol = this.worker.isPlayingRed() ? col : (7 - col);
        return boardData[tRow][tCol];
    }

    public void setGameInProgress(Boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public void handleClick(int row, int col) {
        if (!gameInProgress || !worker.getIsYourTurn())
            return;

        int piece = getBoardPiece(row, col);

//        System.out.println("Piece at row " + row + " and col + " + col + " is " + piece );

        if (piece != 0) {
            selectedRow = row;
            selectedCol = col;

            repaint();
            return;
        }

        if (piece == 0 && selectedCol >= 0 && selectedRow >= 0) {
            this.worker.registerMove(selectedRow, selectedCol, row, col);
        }

        selectedRow = -1;
        selectedCol = -1;

        repaint();
    }

    /**
     * Mouse pressed event
     * @param evt
     */
    public void mousePressed(MouseEvent evt) {
        // TODO: wrong click position
        int col = (evt.getX() - boardBorderWidth) / cellSize;
        int row = (evt.getY() - boardBorderWidth) / cellSize;
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
