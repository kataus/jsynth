package pro.esteps.jsynth.synth_rack.fx;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

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
