package pro.esteps.jsynth.websocket_api.output;

import pro.esteps.jsynth.messaging.message.Message;

/**
 * Сообщение, которое отправляется при каждом изменении текущей ноты в секвенсоре.
 */
public class TickMessage extends Message {

    private int tick;

    public TickMessage(int tick) {
        super("tick");
        this.tick = tick;
    }

    public String getType() {
        return type;
    }

    public int getTick() {
        return tick;
    }

}