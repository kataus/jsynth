package pro.esteps.jsynth.api.output;

import pro.esteps.jsynth.pubsub.message.OutboundWebMessage;

/**
 * Сообщение, которое отправляется при каждом изменении текущей ноты в секвенсоре.
 */
public class TickMessage implements OutboundWebMessage {

    private int tick;

    public TickMessage(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }

}
