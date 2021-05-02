package pro.esteps.jsynth.messaging.broker;

import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.messaging.subscriber.Subscriber;

/**
 * Message broker.
 */
public interface MessageBroker {

    /**
     * Add a new subscriber.
     *
     * @param subscriber
     */
    void addSubscriber(Subscriber subscriber);

    /**
     * Cancel a subscription.
     *
     * @param subscriber
     */
    void removeSubscriber(Subscriber subscriber);

    /**
     * Forward a message to all subscribers.
     *
     * @param message
     */
    void publish(Message message);

}
