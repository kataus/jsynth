package pro.esteps.jsynth.messaging.subscriber;

import pro.esteps.jsynth.messaging.message.Message;

/**
 * Message subscriber.
 */
public interface Subscriber {

    /**
     * Handle a new message event.
     *
     * @param message
     */
    void onMessage(Message message);

}
