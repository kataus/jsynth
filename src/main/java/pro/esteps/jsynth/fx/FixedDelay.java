package pro.esteps.jsynth.fx;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class FixedDelay implements SoundConsumer, SoundProducer {

    private static int CHUNKS_PER_DELAY = 5;

    private byte[] previousInput = new byte[BUFFER_SIZE * CHUNKS_PER_DELAY];

    private SoundProducer producer;

    public FixedDelay(SoundProducer producer) {
        this.producer = producer;
    }

    @Override
    public byte[] getSoundChunk() {

        byte[] mixedChunk = new byte[BUFFER_SIZE];
        byte[] producerChunk = producer.getSoundChunk();

        int sample;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            sample = producerChunk[i] + (previousInput[i] / 4);
            if (sample > Byte.MAX_VALUE) {
                sample = Byte.MAX_VALUE;
            }
            if (sample < Byte.MIN_VALUE) {
                sample = Byte.MIN_VALUE;
            }
            mixedChunk[i] = (byte) sample;
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
