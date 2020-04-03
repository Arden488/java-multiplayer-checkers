/**
 * Player data class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class carries data with player ID
 */


import java.io.Serializable;

public class PlayerData implements Serializable {
    private int playerID;

    /**
     * Constructor
     * @param playerID
     */
    public PlayerData(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Player ID getter
     * @return
     */
    public int getPlayerID() {
        return playerID;
    }
}
