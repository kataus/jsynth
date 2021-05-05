package pro.esteps.jsynth.synth_rack.fx;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;

/**
 * Distortion.
 * <p>
 * WIP.
 */
public class Distortion implements Effect, SoundConsumer, SoundProducer {

    private SoundProducer producer;

    public Distortion(SoundProducer producer) {
        this.producer = producer;
    }

    @Override
    public void setProducer(SoundProducer producer) {
        this.producer = producer;
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        int sample;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            sample = producerChunk[i] * 20;
            // TODO: Use clipping algorithm
            if (sample > Short.MAX_VALUE) {
                sample = Short.MAX_VALUE;
            }
            if (sample < Short.MIN_VALUE) {
                sample = Short.MIN_VALUE;
            }
            mixedChunk[i] = (short) sample;
        }

        return mixedChunk;

    }
}
