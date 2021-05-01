package pro.esteps.jsynth.api.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pro.esteps.jsynth.api.input.SynthMessage;
import pro.esteps.jsynth.api.output.TickMessage;
import pro.esteps.jsynth.pubsub.broker.MessageBroker;
import pro.esteps.jsynth.pubsub.message.Message;
import pro.esteps.jsynth.pubsub.subscriber.Subscriber;

import java.net.InetSocketAddress;

public class SynthServer extends WebSocketServer implements Subscriber {

    private WebSocket currentConn;
    private final ObjectMapper objectMapper;
    private final MessageBroker messageBroker;

    public SynthServer(InetSocketAddress address, ObjectMapper objectMapper, MessageBroker messageBroker) {
        super(address);
        this.objectMapper = objectMapper;
        this.messageBroker = messageBroker;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!");
        System.out.println(
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        System.out.println(conn);
        currentConn = conn;
        System.out.println(currentConn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn + ": " + message);
        try {
            SynthMessage synthMessage = objectMapper.readValue(message, SynthMessage.class);
            messageBroker.publish(synthMessage);
        } catch (JsonProcessingException e) {
            // todo Обработка исключений
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TickMessage) {
            // todo Обработка ситуации, когда текущее соединение отсутствует
            if (currentConn != null) {
                try {
                    String json = objectMapper.writeValueAsString(message);
                    currentConn.send(json);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    // todo Обработка исключения
                }
            }
        }
    }
}
