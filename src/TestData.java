import java.io.Serializable;

public class TestData implements Serializable {
    private String text;

    public TestData(String text) {
        this.text = text;
    }

    public String toString() {
        return this.text;
    }
}