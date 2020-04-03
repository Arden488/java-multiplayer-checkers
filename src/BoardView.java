import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class BoardView extends JPanel implements MouseListener {
    private Client.Worker worker = null;
    private GameView view = null;
    private Boolean gameInProgress = false;
    private Boolean gameOver = false;
    private int winnerID;
    private int[][] boardData;
    private ArrayList<MoveData> allowedMoves;

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
    public BoardView(Client.Worker worker, GameView view) {
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

        // TODO: remove
        if (!gameInProgress || !worker.getIsYourTurn())
            return;
        System.out.println(allowedMoves);
        if (allowedMoves.size() > 0) {
            for (MoveData move : allowedMoves) {
                System.out.println(move);
                int cellXPos = move.getFromCol() * cellSize + boardBorderWidth;
                int cellYPos = move.getFromRow() * cellSize + boardBorderWidth;
                g.setColor(new Color(0, 255, 0, 100));
                g.fillRect(cellXPos, cellYPos, cellSize, cellSize);

                int cellXPos2 = move.getToCol() * cellSize + boardBorderWidth;
                int cellYPos2 = move.getToRow() * cellSize + boardBorderWidth;
                g.setColor(new Color(0, 0, 255, 100));
                g.fillRect(cellXPos2, cellYPos2, cellSize, cellSize);
            }
        }
    }

    public void setBoardData(int[][] board) {
        this.boardData = board;
    }

    public void setAllowedMoves(ArrayList<MoveData> allowedMoves) {
        this.allowedMoves = allowedMoves;
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

        if (gameOver) {
            drawGameOver(g);
        }

        if (!gameInProgress && !gameOver)
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
            case RED_KING:
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(colorMap.get("RED_PIECE"));
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                g.setColor(Color.WHITE);
                // TODO: draw king properly
                g.setFont(new Font("Arial", Font.PLAIN, 12));
                g.drawString("King", pieceXPos + (cellSize / 2 - 1), pieceYPos + (cellSize / 2 - 1));
                break;
            case WHITE_KING:
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos - borderWidth, pieceYPos - borderWidth, pieceWidth + (borderWidth * 2), pieceHeight + (borderWidth * 2));
                g.setColor(colorMap.get("PIECE_SHADOW"));
                g.fillOval(pieceXPos, pieceYPos + shadowSize, pieceWidth, pieceHeight);
                g.setColor(colorMap.get("WHITE_PIECE"));
                g.fillOval(pieceXPos, pieceYPos, pieceWidth, pieceHeight);
                g.setColor(Color.BLACK);
                // TODO: draw king properly
                g.setFont(new Font("Arial", Font.PLAIN, 12));
                g.drawString("King", pieceXPos + (cellSize / 2 - 1), pieceYPos + (cellSize / 2 - 1));
                break;
        }
    }

    private void drawGameOver(Graphics g) {
        String text1 = "GAME OVER";
        String text2 = "WINNER: " + (winnerID == 0 ? "RED" : "WHITE");
        g.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.BOLD, 40);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        int canvasWidth = (cellSize * 8) + (boardBorderWidth * 2);
        int canvasHeight = (cellSize * 8) + (boardBorderWidth * 2);
        int gameOverPosX = (canvasWidth - metrics.stringWidth(text1)) / 2;
        int gameOverPosY = ((canvasHeight - metrics.getHeight()) / 2) + metrics.getAscent() - (metrics.getHeight() / 2);
        int winnerPosX = (canvasWidth - metrics.stringWidth(text2)) / 2;
        int winnerPosY = ((canvasHeight - metrics.getHeight()) / 2) + metrics.getAscent() + (metrics.getHeight() / 2);
        g.drawString(text1, gameOverPosX, gameOverPosY);
        g.drawString(text2, winnerPosX, winnerPosY);
    }

    public void displayWinner(int winnerID) {
        setGameInProgress(false);
        setGameOver(true, winnerID);
    }

    private int getBoardPiece(int row, int col) {
        int tRow = this.worker.isPlayingRed() ? row : (7 - row);
        int tCol = this.worker.isPlayingRed() ? col : (7 - col);
        return boardData[tRow][tCol];
    }

    public void setGameOver(Boolean status, int winnerID) {
        this.gameOver = status;
        this.winnerID = winnerID;
    }

    public void setGameInProgress(Boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public void handleClick(int row, int col) {
        if (!gameInProgress || !worker.getIsYourTurn())
            return;

        int piece = getBoardPiece(row, col);

//        System.out.println("Piece at row " + row + " and col + " + col + " is " + piece );

        for (MoveData move: allowedMoves) {
            if (move.getFromRow() == row && move.getFromCol() == col) {
                selectedRow = row;
                selectedCol = col;

                repaint();
                return;
            }
        }

        for (MoveData move: allowedMoves) {
            if (move.getFromRow() == selectedRow
                    && move.getFromCol() == selectedCol
                    && move.getToRow() == row && move.getToCol() == col) {
                this.worker.registerMove(selectedRow, selectedCol, row, col);

                selectedRow = -1;
                selectedCol = -1;

                repaint();
            }
        }
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
