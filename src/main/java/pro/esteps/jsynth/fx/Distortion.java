package pro.esteps.jsynth.fx;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class Distortion implements Effect, SoundConsumer, SoundProducer {

    private SoundProducer producer;

    public Distortion(SoundProducer producer) {
        this.producer = producer;
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        int sample;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            sample = producerChunk[i] * 20;
            // todo Use clipping algorithm
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
