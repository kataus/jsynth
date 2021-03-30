package pro.esteps.jsynth.mixer;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import java.util.*;
import java.util.stream.Collectors;

public class Mixer implements SoundConsumer, SoundProducer {

    private static class MixerSoundProducerWithVolume {

        private SoundProducer producer;

        // todo Add data type
        private byte volume;

        public MixerSoundProducerWithVolume(SoundProducer producer, byte volume) {
            if (volume < 0 || volume > 100) {
                throw new IllegalArgumentException("Volume out of range 0..100");
            }
            this.producer = producer;
            this.volume = volume;
        }

        public byte[] getMixedSoundChunk() {
            byte[] mixedChunk = new byte[2048];
            if (volume == 0) {
                return mixedChunk;
            }
            byte[] initialChunk = producer.getSoundChunk();
            if (volume == 100) {
                return initialChunk;
            }
            for (int i = 0; i < 2048; i++) {
                mixedChunk[i] = (byte) (initialChunk[i] / 100 * volume);
            }
            return mixedChunk;
        }

        // todo Add changeVolume() method

    }

    private final List<MixerSoundProducerWithVolume> producers;

    public Mixer(int numberOfInputs) {
        assert numberOfInputs > 0;
        producers = new ArrayList<>(Collections.nCopies(numberOfInputs, null));
    }

    public void setProducerForInput(int inputIndex, SoundProducer producer, byte volume) {
        this.producers.set(inputIndex, new MixerSoundProducerWithVolume(producer, volume));
    }

    @Override
    public byte[] getSoundChunk() {

        byte[] chunk = new byte[2048];

        List<MixerSoundProducerWithVolume> activeProducers =
                producers
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        if (activeProducers.isEmpty()) {
            return chunk;
        }

        int sample;

        for (MixerSoundProducerWithVolume producer : activeProducers) {
            byte[] producerChunk = producer.getMixedSoundChunk();
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
