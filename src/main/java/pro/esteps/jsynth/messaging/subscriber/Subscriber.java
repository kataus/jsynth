package pro.esteps.jsynth.messaging.subscriber;

import pro.esteps.jsynth.messaging.message.Message;

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
