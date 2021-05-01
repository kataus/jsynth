package pro.esteps.jsynth.pubsub.broker;

import pro.esteps.jsynth.pubsub.message.Message;
import pro.esteps.jsynth.pubsub.subscriber.Subscriber;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Брокер сообщений.
 */
public class MessageBrokerImpl implements MessageBroker {

    private MessageBrokerImpl instance;

    private List<Subscriber> subscribers;

    private MessageBrokerImpl() {
        // todo Продумать другие варианты потокобезопасных коллекций
        this.subscribers = new CopyOnWriteArrayList<>();
    }

    public MessageBrokerImpl getInstance() {
        if (instance == null) {
            instance = new MessageBrokerImpl();
        }
        return instance;
    }

    /**
     * Добавляет подписчика.
     *
     * @param subscriber Подписчик
     */
    @Override
    public void subscribe(Subscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            synchronized (this) {
                if (!subscribers.contains(subscriber)) {
                    subscribers.add(subscriber);
                }
            }
        }
    }

    /**
     * Отменяет подписку.
     *
     * @param subscriber Подписчик
     */
    @Override
    public void unsubscribe(Subscriber subscriber) {
        if (subscribers.contains(subscriber)) {
            synchronized (this) {
                subscribers.remove(subscriber);
            }
        }
    }

    /**
     * Пересылает сообщение всем подписчикам.
     *
     * @param message Сообщение
     */
    @Override
    public void publish(Message message) {
        for (var subscriber : subscribers) {
            subscriber.onMessage(message);
        }
    }
}
