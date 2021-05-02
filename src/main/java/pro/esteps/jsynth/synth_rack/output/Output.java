package pro.esteps.jsynth.synth_rack.output;

import pro.esteps.jsynth.websocket_api.output.SequencerStepMessage;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.messaging.broker.MessageBroker;

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
    private final MessageBroker messageBroker;

    public Output(SoundProducer producer, MessageBroker messageBroker) {
        this.producer = producer;
        this.messageBroker = messageBroker;
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
                    messageBroker.publish(new SequencerStepMessage(tickIndex++));
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
