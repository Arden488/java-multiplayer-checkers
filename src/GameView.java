/**
 * Game view class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class responsible for the game display (frame, buttons etc.)
 */

// TODO: import only required
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class GameView extends JFrame {
    private int cellSize = 60;
    private int cellAxisCount = 8;
    private int windowPanelOffset = 22;
    private int boardBorderWidth = 20;
    private int optionsPanelMargin = 200;

    // TODO: remove
    private JLabel logTextLabel = new JLabel("Empty log");
    private JLabel payloadLabel = new JLabel("Empty payload");

    private HashMap<String, Color> colorMap = new HashMap<String, Color>();

    /**
     * Constructor
     */
    public GameView() {
        setupColors();
        setupFrame();

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
        BoardView boardDisplay = new BoardView();
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
        JButton newGameButton = new JButton("New Game");
        optionsPanel.add(newGameButton, gbc);

        // TODO: remove
        JLabel logTitleLabel = new JLabel("Log:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10,10,0,10);
        optionsPanel.add(logTitleLabel, gbc);

        // Create a status message container
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0,10,20,10);
        optionsPanel.add(this.logTextLabel, gbc);

        // TODO: remove
        JLabel payloadTitleLabel = new JLabel("Payload:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10,10,0,10);
        optionsPanel.add(payloadTitleLabel, gbc);

        // TODO: remove
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(0,10,20,10);
        optionsPanel.add(this.payloadLabel, gbc);

        return optionsPanel;
    }
}
