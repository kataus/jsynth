package pro.esteps.jsynth.pubsub.subscriber;

import pro.esteps.jsynth.pubsub.message.Message;

/**
 * Подписчик на сообщения.
 */
public interface Subscriber {

    /**
     * Выполняет действие в ответ на сообщение.
     *
     * @param message Сообщение
     */
    void onMessage(Message message);

}
