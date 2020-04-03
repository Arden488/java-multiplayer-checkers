/**
 * Data class
 * Author: Anton Samoilov <2459087s@student.gla.ac.uk>, matric 2459087S
 * ------------
 * This class carries data that is transferred between client and server.
 * Payload accepts different objects. Type is a ExchangeEvent label to help event handlers
 * identify the type of payload
 */

import java.io.Serializable;

public class Data<T> implements Serializable {
    private T payload = null;
    private ExchangeEvent type;

    /**
     * Constructor
     * @param type
     */
    public Data(ExchangeEvent type) {
        this.type = type;
    }

    /**
     * Type getter
     * @return String type
     */
    public ExchangeEvent getType() {
        return type;
    }

    /**
     * Payload getter
     * @return T payload
     */
    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
