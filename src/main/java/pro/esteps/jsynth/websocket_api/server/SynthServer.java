package pro.esteps.jsynth.websocket_api.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pro.esteps.jsynth.websocket_api.mapper.MessageMapper;
import pro.esteps.jsynth.websocket_api.output.SequencerStepMessage;
import pro.esteps.jsynth.messaging.broker.MessageBroker;
import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.messaging.subscriber.Subscriber;

import java.net.InetSocketAddress;

/**
 * WebSocket synth server.
 */
public class SynthServer extends WebSocketServer implements Subscriber {

    private WebSocket currentConn;
    private final MessageMapper messageMapper;
    private final MessageBroker messageBroker;

    public SynthServer(InetSocketAddress address,
                       MessageMapper messageMapper,
                       MessageBroker messageBroker) {
        super(address);
        this.messageMapper = messageMapper;
        this.messageBroker = messageBroker;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // TODO: Send a handshake message to the client
        // TODO: Disallow more than 1 connection
        currentConn = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // TODO: Stop sound
    }

    @Override
    public void onMessage(WebSocket conn, String json) {
        var message = messageMapper.mapJsonToMessage(json);
        messageBroker.publish(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        // TODO: These 2 lines of code were taken from the library examples; check whether they make sense
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(10);
    }

    /**
     * Handle a PubSub message.
     *
     * @param message
     */
    public void onMessage(Message message) {
        if (message instanceof SequencerStepMessage) {
            // TODO: Handle possible null value for currentConn
            if (currentConn != null) {
                String json = messageMapper.mapMessageToJson(message);
                currentConn.send(json);
            }
        }
    }
}
