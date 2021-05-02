package pro.esteps.jsynth.synth_rack.message_handler;

import pro.esteps.jsynth.messaging.message.Message;

/**
 * PubSub message handler.
 */
public interface MessageHandler {

    void handleMessage(Message message);

}
