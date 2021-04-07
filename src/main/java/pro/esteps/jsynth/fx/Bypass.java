package pro.esteps.jsynth.fx;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

public class Bypass implements Effect, SoundConsumer, SoundProducer {

    private SoundProducer producer;

    public Bypass(SoundProducer producer) {
        this.producer = producer;
    }

    @Override
    public short[] getSoundChunk() {
        return producer.getSoundChunk();
    }
}
