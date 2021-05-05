package pro.esteps.jsynth.synth_rack.output;

import pro.esteps.jsynth.websocket_api.output.SequencerStepMessage;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.messaging.broker.MessageBroker;

import javax.sound.sampled.SourceDataLine;

import java.nio.ByteBuffer;

import static pro.esteps.jsynth.App.*;
import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;
import static pro.esteps.jsynth.synth_rack.config.Config.TICKS_PER_SEQUENCER_STEP;

/**
 * This class produces sound output to the AudioSystem source data line.
 */
public class Output implements Runnable {

    private final SourceDataLine soundLine;
    private final SoundProducer producer;
    private final MessageBroker messageBroker;

    private final byte[] buffer;
    private final ByteBuffer byteBuffer;

    private int tick;
    private int sequencerStep;

    public Output(SourceDataLine soundLine, SoundProducer producer, MessageBroker messageBroker) {
        this.soundLine = soundLine;
        this.producer = producer;
        this.messageBroker = messageBroker;
        this.buffer = new byte[BUFFER_SIZE * 2];
        this.byteBuffer = ByteBuffer.allocate(2);
    }

    public void run() {
        // TODO: Add interrupts
        soundLine.start();
        do {
            outputSoundChunk(producer.getSoundChunk());
            processTick();
        } while (true);
    }

    /**
     * Send a sound chunk to the AudioSystem source data line.
     *
     * @param chunk
     */
    private void outputSoundChunk(short[] chunk) {
        for (int i = 0; i < BUFFER_SIZE; i++) {
            byteBuffer.putShort(0, chunk[i]);
            buffer[i * 2 + 1] = byteBuffer.get(0);
            buffer[i * 2] = byteBuffer.get(1);
        }
        soundLine.write(buffer, 0, BUFFER_SIZE * 2);
    }

    /**
     * Process a tick.
     * <p>
     * To ensure precise tempo accuracy, buffer readouts are currently used to sync the application,
     * and each readout triggers a new tick.
     */
    private void processTick() {
        if (tick == 0) {
            messageBroker.publish(new SequencerStepMessage(sequencerStep++));
            if (sequencerStep == 64) {
                sequencerStep = 0;
            }
        }
        tick++;
        if (tick == TICKS_PER_SEQUENCER_STEP) {
            tick = 0;
        }
    }

}
