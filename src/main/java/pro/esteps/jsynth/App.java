package pro.esteps.jsynth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import pro.esteps.jsynth.api.server.SynthServer;
import pro.esteps.jsynth.console.TestConsole;
import pro.esteps.jsynth.pubsub.broker.MessageBroker;
import pro.esteps.jsynth.pubsub.broker.MessageBrokerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static int SAMPLE_RATE = 44100;
    public static int BUFFER_SIZE = 1024;

    private static class ServerThread extends Thread {

        private SynthServer synthServer;

        private ServerThread(SynthServer synthServer) {
            this.synthServer = synthServer;
        }

        @Override
        public void run() {
            synthServer.run();
        }

    }

    public static void main(String[] args) throws IOException {

        MessageBroker messageBroker = MessageBrokerImpl.getInstance();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String host = "localhost";
        int port = 8887;
        SynthServer server = new SynthServer(new InetSocketAddress(host, port), objectMapper, messageBroker);

        messageBroker.addSubscriber(server);

        Thread thread = new ServerThread(server);
        thread.start();

        TestConsole console = new TestConsole(messageBroker);
        console.processConsoleInput();

    }
}
