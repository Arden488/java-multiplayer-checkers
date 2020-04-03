/**
 * Game view class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class is responsible for the UI display
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameView extends JFrame implements ActionListener, ViewSettings {
    // Instance variables
    private Client.Worker worker = null;
    private BoardView boardDisplay = null;
    private JButton newGameButton;
    // TODO: rename
    private JLabel logTextLabel = new JLabel("");

    /**
     * Constructor
     * Prepare the frame
     */
    public GameView() {
        int frameWidth = 8 * CELL_SIZE + (BOARD_BORDER_WIDTH * 2);
        int frameHeight = 8 * CELL_SIZE + (BOARD_BORDER_WIDTH * 2) + 100;
        this.setSize(frameWidth, frameHeight);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(BACKGROUND_COLOR);
    }

    /**
     * Create a layout and attach it to the frame
     */
    public void drawLayout() {
        JPanel layout = createMainLayout();
        this.add(layout);

        this.setVisible(true);
    }

    /**
     * Create general game layout panel
     * @return JPanel layout panel
     */
    private JPanel createMainLayout() {
        JPanel layoutPanel = new JPanel(new BorderLayout());

        // Paint and display the board
        boardDisplay = new BoardView(worker);
        layoutPanel.add(boardDisplay, BorderLayout.CENTER);

        // Create and attach options panel
        JPanel options = createOptionsPanel();
        layoutPanel.add(options, BorderLayout.SOUTH);

        return layoutPanel;
    }

    /**
     * Create options panel
     * @return JPanel options panel
     */
    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel();

        // Setup layout settings
        GridBagLayout optionsLayout = new GridBagLayout();
        optionsPanel.setLayout(optionsLayout);
        GridBagConstraints gbc = new GridBagConstraints();

        // Create new game button and bind a listener to it
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10,10,10,10);
        newGameButton = new JButton("New Game");
        newGameButton.setEnabled(false);
        newGameButton.addActionListener(this);
        optionsPanel.add(newGameButton, gbc);

        // Create a status message container
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0,10,20,10);
        optionsPanel.add(this.logTextLabel, gbc);

        return optionsPanel;
    }

    /**
     * Method to process the click event
     * @param evt
     */
    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == newGameButton) {
            this.worker.registerNewGameRequest();
            newGameButton.setEnabled(false);
            this.logTextLabel.setText("Now your opponent needs to press Start New Game");
        }
    }

    /**
     * Process the WAITING_FOR_OPPONENT event
     * Set the text to inform the user
     */
    public void handleAwaitingOpponent() {
        this.logTextLabel.setText("Welcome! Waiting for the opponent to connect...");
    }

    /**
     * Process the REQUEST_NEW_GAME event
     * Set the text to inform the user and enable the new game button
     */
    public void handleRequestNewGame() {
        this.newGameButton.setEnabled(true);
        this.logTextLabel.setText("Press New Game to start");
    }

    /**
     * Process the NEW_GAME event
     * Set the message who is an active player
     * and pass data to the board graphics
     * @param data
     */
    public void handleNewGame(Data data) {
        NewRoundData newRoundData = (NewRoundData) data.getPayload();

        changeActivePlayerMessage();

        // Reset the board state
        this.boardDisplay.setGameInProgress(true);
        this.boardDisplay.setGameOver(false, -1);
        // Pass data to the board graphics and do repaint
        this.boardDisplay.setBoardData(newRoundData.getBoardState());
        this.boardDisplay.setAllowedMoves(newRoundData.getAllowedMoves());
        this.boardDisplay.updateBoard();
    }

    /**
     * Set the message for every player if he can make a move or should wait
     */
    public void changeActivePlayerMessage() {
        if (worker.getIsYourTurn()) {
            this.logTextLabel.setText("Your turn. Make a move!");
        } else {
            this.logTextLabel.setText("It is your opponent's turn. Waiting...");
        }
    }

    /**
     * Process the GAME_OVER event
     * Display the message on the board
     * and enable new game button
     * @param data
     */
    public void handleGameOver(Data data) {
        GameOverData gameOver = (GameOverData) data.getPayload();

        this.boardDisplay.displayWinner(gameOver.getWinnerID());
        this.boardDisplay.updateBoard();

        this.newGameButton.setEnabled(true);
        this.logTextLabel.setText("Press New Game to start");
    }

    /**
     * Process the NEW_ROUND event
     * Set the message who is an active player
     * @param data
     */
    public void handleNewRound(Data data) {
        NewRoundData newRoundData = (NewRoundData) data.getPayload();

        changeActivePlayerMessage();

        // Pass data to the board graphics and do repaint
        this.boardDisplay.setBoardData(newRoundData.getBoardState());
        this.boardDisplay.setAllowedMoves(newRoundData.getAllowedMoves());
        this.boardDisplay.updateBoard();
    }

    /**
     * Worker setter
     * @param worker
     */
    public void setWorker(Client.Worker worker) {
        this.worker = worker;
    }
}
