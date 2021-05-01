package pro.esteps.jsynth.output;

import pro.esteps.jsynth.api.server.SynthServer;
import pro.esteps.jsynth.contract.SoundProducer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import java.nio.ByteBuffer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

public class Output implements Runnable {

    // todo Duplicate code
    private static final int CHUNKS_PER_NOTE = 5;

    private static final AudioFormat FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            SAMPLE_RATE,
            16,
            1,
            2,
            SAMPLE_RATE,
            false
    );

    private final SoundProducer producer;
    private final SynthServer webSocketServer;

    public Output(SoundProducer producer, SynthServer webSocketServer) {
        this.producer = producer;
        this.webSocketServer = webSocketServer;
    }

    // todo Process interrupts
    public void run() {

        SourceDataLine soundLine;

        try {

            soundLine = AudioSystem.getSourceDataLine(FORMAT);
            final byte[] buffer = new byte[BUFFER_SIZE * 2];
            soundLine.open(FORMAT, BUFFER_SIZE * 2);
            soundLine.start();

            ByteBuffer bb = ByteBuffer.allocate(2);

            int counter = 0;
            int tickIndex = 0;
            do {
                short[] chunk = producer.getSoundChunk();
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    bb.putShort(0, chunk[i]);
                    buffer[i * 2 + 1] = bb.get(0);
                    buffer[i * 2] = bb.get(1);
                }
                soundLine.write(buffer, 0, BUFFER_SIZE * 2);
                if (counter == 0) {
                    webSocketServer.sendMessage("tick " + tickIndex++);
                    if (tickIndex == 16) {
                        tickIndex = 0;
                    }
                }
                counter++;
                if (counter == CHUNKS_PER_NOTE) {
                    counter = 0;
                }
            } while (true);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

}
