package pro.esteps.jsynth.synth_rack.fx;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

/**
 * Bypass.
 * <p>
 * This effect does nothing and simply passes sound chunks to its consumers.
 */
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
