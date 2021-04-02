package pro.esteps.jsynth.amplitude;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class SimpleDecay implements SoundConsumer, SoundProducer {

    // todo Duplicate code
    private static final int CHUNKS_PER_NOTE = 5;

    private final SoundProducer producer;

    private byte[] envelope = new byte[0];

    private int envelopeIndex;

    public SimpleDecay(SoundProducer producer) {
        this.producer = producer;
        regenerateEnvelope();
    }

    private void regenerateEnvelope() {
        // todo Add dynamic calculations
        int envelopeSize = BUFFER_SIZE * CHUNKS_PER_NOTE;
        envelope = new byte[envelopeSize];
        for (int i = 0; i < envelopeSize; i++) {
            envelope[i] = (byte) ((envelopeSize - i) * 100 / envelopeSize);
        }
        int a = 1;
    }

    public void resetIndex() {
        envelopeIndex = 0;
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];

        if (envelopeIndex >= envelope.length - 1) {
            return mixedChunk;
        }

        short[] producerChunk = producer.getSoundChunk();

        for (int i = 0; i < BUFFER_SIZE; i++) {
            mixedChunk[i] = (short) (producerChunk[i] / 100 * envelope[envelopeIndex++]);
            if (envelopeIndex >= envelope.length - 1) {
                break;
            }
        }

        return mixedChunk;
    }
}
