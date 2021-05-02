package pro.esteps.jsynth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import pro.esteps.jsynth.websocket_api.mapper.MessageMapperImpl;
import pro.esteps.jsynth.websocket_api.server.SynthServer;
import pro.esteps.jsynth.messaging.broker.MessageBrokerImpl;
import pro.esteps.jsynth.synth_rack.SynthRackImpl;

import java.net.InetSocketAddress;

public class App {

    public static final int SAMPLE_RATE = 44100;
    public static final int BUFFER_SIZE = 1024;
    public static final int TICKS_PER_NOTE = 5;

    public static final String WEBSOCKET_HOST = "localhost";
    public static final int WEBSOCKET_PORT = 8887;

    public static void main(String[] args) {

        var messageBroker = MessageBrokerImpl.getInstance();

        var objectMapper = new ObjectMapper();
        // TODO: consider other options to address the partial JSON issue
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);

        var messageMapper = new MessageMapperImpl(objectMapper);

        var inetSocketAddress = new InetSocketAddress(WEBSOCKET_HOST, WEBSOCKET_PORT);
        var server = new SynthServer(
                inetSocketAddress,
                messageMapper,
                messageBroker);

        var synthRack = SynthRackImpl.getInstance(messageBroker);

        messageBroker.addSubscriber(server);
        messageBroker.addSubscriber(synthRack);

        server.run();

    }
}
