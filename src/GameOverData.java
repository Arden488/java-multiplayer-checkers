/**
 * Game over data class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class carries data about the winner in case of the game over event
 */

import java.io.Serializable;

public class GameOverData implements Serializable {
    private int winnerID;

    /**
     * Constructor
     * @param winnerID
     */
    public GameOverData(int winnerID) {
        this.winnerID = winnerID;
    }

    /**
     * Winner ID getter
     * @return
     */
    public int getWinnerID() {
        return winnerID;
    }
}
