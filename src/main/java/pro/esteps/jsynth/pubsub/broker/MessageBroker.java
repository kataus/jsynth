package pro.esteps.jsynth.pubsub.broker;

import pro.esteps.jsynth.pubsub.message.Message;
import pro.esteps.jsynth.pubsub.subscriber.Subscriber;

/**
 * Брокер сообщений.
 */
public interface MessageBroker {

    /**
     * Добавляет подписчика.
     *
     * @param subscriber Подписчик
     */
    void subscribe(Subscriber subscriber);

    /**
     * Отменяет подписку.
     *
     * @param subscriber Подписчик
     */
    void unsubscribe(Subscriber subscriber);

    /**
     * Пересылает сообщение всем подписчикам.
     *
     * @param message Сообщение
     */
    void publish(Message message);

}
