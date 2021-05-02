package pro.esteps.jsynth.messaging.message;

/**
 * Message.
 */
public class Message {

    public Message() {
    }

    // TODO: Use enum instead of String
    protected String type;

    protected Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
