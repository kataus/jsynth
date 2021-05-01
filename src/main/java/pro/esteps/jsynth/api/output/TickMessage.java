package pro.esteps.jsynth.api.output;

import pro.esteps.jsynth.pubsub.message.Message;

/**
 * Сообщение, которое отправляется при каждом изменении текущей ноты в секвенсоре.
 */
public class TickMessage implements Message {

    // todo Use enum
    private String type;
    private int tick;

    public TickMessage(int tick) {
        this.type = "tick";
        this.tick = tick;
    }

    public String getType() {
        return type;
    }

    public int getTick() {
        return tick;
    }

}
