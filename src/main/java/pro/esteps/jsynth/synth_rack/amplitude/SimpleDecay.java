package pro.esteps.jsynth.synth_rack.amplitude;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;
import static pro.esteps.jsynth.synth_rack.config.Config.TICKS_PER_SEQUENCER_STEP;

/**
 * Simple decay.
 */
public class SimpleDecay implements SoundConsumer, SoundProducer {

    private final SoundProducer producer;
    private byte decayLength;
    private byte[] envelope = new byte[0];
    private int envelopeIndex;

    public SimpleDecay(SoundProducer producer, byte decayLength) {
        this.producer = producer;
        this.decayLength = decayLength;
        regenerateEnvelope();
    }

    private void regenerateEnvelope() {
        // TODO: Add dynamic calculations
        int envelopeSize;
        if (decayLength == 0) {
            envelopeSize = BUFFER_SIZE * TICKS_PER_SEQUENCER_STEP;
        } else {
            envelopeSize = decayLength * BUFFER_SIZE * TICKS_PER_SEQUENCER_STEP;
        }
        envelope = new byte[envelopeSize];
        if (decayLength == 0) {
            for (int i = 0; i < envelopeSize; i++) {
                envelope[i] = (byte) 100;
            }
        } else {
            for (int i = 0; i < envelopeSize; i++) {
                envelope[i] = (byte) ((envelopeSize - i) * 100 / envelopeSize);
            }
        }
    }

    public void setDecayLength(byte decayLength) {
        if (this.decayLength != decayLength) {
            this.decayLength = decayLength;
            regenerateEnvelope();
        }
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
