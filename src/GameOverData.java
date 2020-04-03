import java.io.Serializable;

public class GameOverData implements Serializable {
    private int winnerID;

    public GameOverData(int winnerID) {
        this.winnerID = winnerID;
    }

    public int getWinnerID() {
        return winnerID;
    }
}
