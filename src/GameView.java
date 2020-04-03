import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class GameView extends JFrame implements ActionListener {
    private Client.Worker worker = null;

    private BoardView boardDisplay = null;

    private int cellSize = 60;
    private int cellAxisCount = 8;
    private int windowPanelOffset = 22;
    private int boardBorderWidth = 20;
    private int optionsPanelMargin = 100;

    private JButton newGameButton;
    // TODO: rename
    private JLabel logTextLabel = new JLabel("");

    private HashMap<String, Color> colorMap = new HashMap<String, Color>();

    /**
     * Constructor
     */
    public GameView() {
        setupColors();
        setupFrame();
    }

    public void drawLayout() {
        JPanel layout = createMainLayout();
        this.add(layout);

        this.setVisible(true);
    }

    /**
     * Method to set frame settings
     */
    private void setupFrame() {
        int frameWidth = cellAxisCount * cellSize + (boardBorderWidth * 2);
        int frameHeight = cellAxisCount * cellSize + windowPanelOffset + (boardBorderWidth * 2) + optionsPanelMargin;
        this.setSize(frameWidth, frameHeight);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.BLACK);
    }

    /**
     * Method to add colors to the color hash map
     */
    private void setupColors() {
        colorMap.put("CELL_ODD", new Color(255, 239, 188));
        colorMap.put("CELL_EVEN", new Color(85, 138, 28));
        colorMap.put("RED_PIECE", new Color(197, 0, 0));
        colorMap.put("SELECTED", new Color(255, 255, 255, 150));
        colorMap.put("WHITE_PIECE", Color.WHITE);
        colorMap.put("PIECE_SHADOW", Color.BLACK);
    }

    /**
     * Create general game layout panel
     * @return JPanel layout panel
     */
    private JPanel createMainLayout() {
        JPanel layoutPanel = new JPanel(new BorderLayout());

        // Paint and display the board
        boardDisplay = new BoardView(worker, this);
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

        // Create new game button
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

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == newGameButton) {
            this.worker.registerNewGameRequest();
            newGameButton.setEnabled(false);
            this.logTextLabel.setText("Now your opponent needs to press Start New Game");
        }
//        else if (src == resignButton)
//            doResign();
    }

    public void handleAwaitingOpponent() {
        this.logTextLabel.setText("Welcome! Waiting for the opponent to connect...");
    }

    public void handleRequestNewGame() {
        this.newGameButton.setEnabled(true);
        this.logTextLabel.setText("Press New Game to start");
    }

    public void handleNewGame(Data data) {
        NewRoundData newRoundData = (NewRoundData) data.getPayload();

        changeActivePlayerMessage();

        this.boardDisplay.setGameInProgress(true);
        this.boardDisplay.setGameOver(false, -1);
        this.boardDisplay.setBoardData(newRoundData.getBoardState());
        this.boardDisplay.setAllowedMoves(newRoundData.getAllowedMoves());
        this.boardDisplay.updateBoard();
    }

    public void changeActivePlayerMessage() {
        if (worker.getIsYourTurn()) {
            this.logTextLabel.setText("Your turn! Take a move");
        } else {
            this.logTextLabel.setText("It is your opponent's turn. Waiting...");
        }
    }

    public void handleGameOver(Data data) {
        GameOverData gameOver = (GameOverData) data.getPayload();

        this.boardDisplay.displayWinner(gameOver.getWinnerID());
        this.boardDisplay.updateBoard();

        this.newGameButton.setEnabled(true);
        this.logTextLabel.setText("Press New Game to start");
    }

    public void handleNewRound(Data data) {
        NewRoundData newRoundData = (NewRoundData) data.getPayload();

        changeActivePlayerMessage();

        this.boardDisplay.setBoardData(newRoundData.getBoardState());
        this.boardDisplay.setAllowedMoves(newRoundData.getAllowedMoves());
        this.boardDisplay.updateBoard();
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getBoardBorderWidth() {
        return boardBorderWidth;
    }

    public int getWindowPanelOffset() {
        return windowPanelOffset;
    }

    public void setWorker(Client.Worker worker) {
        this.worker = worker;
    }

    public HashMap<String, Color> getColorMap() {
        return colorMap;
    }
}
