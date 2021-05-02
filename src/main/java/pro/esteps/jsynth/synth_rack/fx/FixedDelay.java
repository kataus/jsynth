package pro.esteps.jsynth.synth_rack.fx;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.lib.iirj.Butterworth;

import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;
import static pro.esteps.jsynth.synth_rack.config.Config.SAMPLE_RATE;

/**
 * Fixed delay.
 * <p>
 * This effect adds a delay to the sound.
 */
public class FixedDelay implements Effect, SoundConsumer, SoundProducer {

    private static final int DELAY_LINES = 3;

    private static final int TICKS_PER_DELAY = 22;

    private final short[] previousInput = new short[DELAY_LINES * BUFFER_SIZE * TICKS_PER_DELAY];

    // TODO: Attach LowPassFilter instead of Butterworth
    private final Butterworth butterworth;

    private final SoundProducer producer;

    public FixedDelay(SoundProducer producer) {
        this.producer = producer;
        this.butterworth = new Butterworth();
        butterworth.lowPass(8, SAMPLE_RATE, 1500);
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        int sample;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            sample = 0;
            sample += (previousInput[TICKS_PER_DELAY * BUFFER_SIZE * 2 + i] / 2);
            sample += (previousInput[TICKS_PER_DELAY * BUFFER_SIZE + i] / 4);
            sample += (previousInput[i] / 8);
            sample = (int) butterworth.filter(sample);
            sample += producerChunk[i];
            // TODO: Use clipping algorithm
            if (sample > Short.MAX_VALUE) {
                sample = Short.MAX_VALUE;
            }
            if (sample < Short.MIN_VALUE) {
                sample = Short.MIN_VALUE;
            }
            mixedChunk[i] = (short) sample;
        }

        System.arraycopy(
                previousInput,
                BUFFER_SIZE,
                previousInput,
                0,
                DELAY_LINES * BUFFER_SIZE * (TICKS_PER_DELAY - 1)
        );
        System.arraycopy(
                producerChunk,
                0,
                previousInput,
                DELAY_LINES * BUFFER_SIZE * (TICKS_PER_DELAY - 1),
                BUFFER_SIZE
        );

        return mixedChunk;
    }
}
