package pro.esteps.jsynth.messaging.broker;

import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.messaging.subscriber.Subscriber;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Message broker.
 */
public class MessageBrokerImpl implements MessageBroker {

    private static MessageBrokerImpl instance;

    private final List<Subscriber> subscribers;

    private MessageBrokerImpl() {
        // TODO: Consider other thread-safe alternatives
        this.subscribers = new CopyOnWriteArrayList<>();
    }

    public static MessageBrokerImpl getInstance() {
        if (instance == null) {
            instance = new MessageBrokerImpl();
        }
        return instance;
    }

    /**
     * Add a new subscriber.
     *
     * @param subscriber
     */
    @Override
    public void addSubscriber(Subscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            synchronized (this) {
                if (!subscribers.contains(subscriber)) {
                    subscribers.add(subscriber);
                }
            }
        }
    }

    /**
     * Cancel a subscription.
     *
     * @param subscriber
     */
    @Override
    public void removeSubscriber(Subscriber subscriber) {
        if (subscribers.contains(subscriber)) {
            synchronized (this) {
                subscribers.remove(subscriber);
            }
        }
    }

    /**
     * Forward a message to all subscribers.
     *
     * @param message
     */
    @Override
    public void publish(Message message) {
        for (var subscriber : subscribers) {
            subscriber.onMessage(message);
        }
    }
}
