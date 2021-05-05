package pro.esteps.jsynth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import pro.esteps.jsynth.websocket_api.mapper.MessageMapperImpl;
import pro.esteps.jsynth.websocket_api.server.SynthServer;
import pro.esteps.jsynth.messaging.broker.MessageBrokerImpl;
import pro.esteps.jsynth.synth_rack.SynthRackImpl;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.net.InetSocketAddress;

import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;
import static pro.esteps.jsynth.synth_rack.config.Config.FORMAT;

public class App {

    public static final String WEBSOCKET_HOST = "localhost";
    public static final int WEBSOCKET_PORT = 8887;

    public static void main(String[] args) {

        try (SourceDataLine soundLine = AudioSystem.getSourceDataLine(FORMAT)) {

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

            soundLine.open(FORMAT, BUFFER_SIZE * 2);
            var synthRack = SynthRackImpl.getInstance(soundLine, messageBroker);

            messageBroker.addSubscriber(server);
            messageBroker.addSubscriber(synthRack);

            server.run();

        } catch (Exception e) {
            // TODO: Handle exception
        }



    }
}
