import java.io.Serializable;

public class PlayerData implements Serializable {
    private int playerID;

    public PlayerData(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }
}
