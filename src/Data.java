/**
 * Data class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class carries data that is transferred between client and server.
 * Payload accepts different objects. Type is a string to help event handlers
 * identify the type of payload
 */

import java.io.Serializable;

public class Data<T> implements Serializable {
    private T payload;
    private String type;

    /**
     * Constructor
     * @param payload
     * @param type
     */
    public Data(T payload, String type) {
        this.payload = payload;
        this.type = type;
    }

    /**
     * Type getter
     * @return String type
     */
    public String getType() {
        return type;
    }

    /**
     * Payload getter
     * @return T payload
     */
    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Data {" +
                "payload=" + payload +
                ", type='" + type + '\'' +
                '}';
    }
}
