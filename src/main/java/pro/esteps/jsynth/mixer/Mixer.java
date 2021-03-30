package pro.esteps.jsynth.mixer;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import java.util.LinkedHashSet;
import java.util.Set;

public class Mixer implements SoundConsumer, SoundProducer {

    private final Set<SoundProducer> producers = new LinkedHashSet<>();

    public void addProducer(SoundProducer producer) {
        this.producers.add(producer);
    }

    @Override
    public byte[] getSoundChunk() {

        byte[] chunk = new byte[2048];

        if (producers.isEmpty()) {
            return chunk;
        }

        int sample;

        for (SoundProducer producer : producers) {
            byte[] producerChunk = producer.getSoundChunk();
            for (int i = 0; i < 2048; i++) {
                sample = chunk[i] + producerChunk[i];
                if (sample > Byte.MAX_VALUE) {
                    sample = Byte.MAX_VALUE;
                } else if (sample < Byte.MIN_VALUE) {
                    sample = Byte.MIN_VALUE;
                }
                chunk[i] = (byte) sample;
            }
        }

        return chunk;
    }

}
