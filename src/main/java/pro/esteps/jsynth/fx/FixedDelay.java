package pro.esteps.jsynth.fx;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.iirj.Butterworth;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

public class FixedDelay implements Effect, SoundConsumer, SoundProducer {

    private static final int DELAY_LINES = 3;

    // todo Duplicate code
    private static final int CHUNKS_PER_DELAY = 22;

    private final short[] previousInput = new short[DELAY_LINES * BUFFER_SIZE * CHUNKS_PER_DELAY];

    // todo Attach LowPassFilter instead of Butterworth
    private Butterworth butterworth;

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
            sample += (previousInput[CHUNKS_PER_DELAY * BUFFER_SIZE * 2 + i] / 2);
            sample += (previousInput[CHUNKS_PER_DELAY * BUFFER_SIZE + i] / 4);
            sample += (previousInput[i] / 8);
            sample = (int) butterworth.filter(sample);
            sample += producerChunk[i];
            // todo Use clipping algorithm
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
                DELAY_LINES * BUFFER_SIZE * (CHUNKS_PER_DELAY - 1)
        );
        System.arraycopy(
                producerChunk,
                0,
                previousInput,
                DELAY_LINES * BUFFER_SIZE * (CHUNKS_PER_DELAY - 1),
                BUFFER_SIZE
        );

        return mixedChunk;
    }
}
