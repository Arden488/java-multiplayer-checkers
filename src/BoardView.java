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

public class BoardView extends JPanel implements MouseListener {
    private ClientWorker worker = null;

    /**
     * Constructor
     */
    public BoardView(ClientWorker worker) {
        this.worker = worker;
        this.addMouseListener(this);
    }

    /**
     * Method to paint the board
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Mouse pressed event
     * @param evt
     */
    public void mousePressed(MouseEvent evt) {
        String message = "X: " + evt.getX() + ", Y: " + evt.getY();
        Data data = new Data(new TestData(message), "test");
        this.worker.dispatchData(data);
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
