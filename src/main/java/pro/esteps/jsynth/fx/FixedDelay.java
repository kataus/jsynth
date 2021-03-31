package pro.esteps.jsynth.fx;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class FixedDelay implements SoundConsumer, SoundProducer {

    private static int CHUNKS_PER_DELAY = 5;

    private short[] previousInput = new short[BUFFER_SIZE * CHUNKS_PER_DELAY];

    private SoundProducer producer;

    public FixedDelay(SoundProducer producer) {
        this.producer = producer;
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        int sample;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            sample = producerChunk[i] + (previousInput[i] / 4);
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
                BUFFER_SIZE * (CHUNKS_PER_DELAY - 1)
        );
        System.arraycopy(
                producerChunk,
                0,
                previousInput,
                BUFFER_SIZE * (CHUNKS_PER_DELAY - 1),
                BUFFER_SIZE
        );

        return mixedChunk;
    }
}
