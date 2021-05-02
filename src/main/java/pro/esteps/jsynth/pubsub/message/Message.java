package pro.esteps.jsynth.pubsub.message;

public class Message {

    public Message() {
    }

    // todo Use enum
    protected String type;

    protected Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
