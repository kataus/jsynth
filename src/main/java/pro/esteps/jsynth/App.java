package pro.esteps.jsynth;

import pro.esteps.jsynth.api.server.SynthServer;
import pro.esteps.jsynth.console.TestConsole;

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

        String host = "localhost";
        int port = 8887;
        SynthServer server = new SynthServer(new InetSocketAddress(host, port));
        Thread thread = new ServerThread(server);
        thread.start();

        TestConsole console = new TestConsole(server);
        console.processConsoleInput();



    }
}
