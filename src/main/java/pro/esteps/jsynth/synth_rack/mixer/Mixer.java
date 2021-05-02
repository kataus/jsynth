package pro.esteps.jsynth.synth_rack.mixer;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

import java.util.*;
import java.util.stream.Collectors;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class Mixer implements SoundConsumer, SoundProducer {

    private static class MixerSoundProducerWithVolume {

        private SoundProducer producer;

        // todo Add data type
        // todo Use log. volume control
        private byte volume;

        public MixerSoundProducerWithVolume(SoundProducer producer, byte volume) {
            assert volume >= 0 && volume <= 100;
            this.producer = producer;
            this.volume = volume;
        }

        public short[] getMixedSoundChunk() {

            short[] mixedChunk = new short[BUFFER_SIZE];
            if (volume == 0) {
                return mixedChunk;
            }

            short[] initialChunk = producer.getSoundChunk();
            if (volume == 100) {
                System.arraycopy(initialChunk, 0, mixedChunk, 0, BUFFER_SIZE);
                return mixedChunk;
            }

            for (int i = 0; i < BUFFER_SIZE; i++) {
                mixedChunk[i] = (short) (initialChunk[i] / 100 * volume);
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
    public short[] getSoundChunk() {

        short[] chunk = new short[BUFFER_SIZE];

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
            short[] producerChunk = producer.getMixedSoundChunk();
            for (int i = 0; i < BUFFER_SIZE; i++) {
                sample = chunk[i] + producerChunk[i];
                // todo Use clipping algorithm
                if (sample > Short.MAX_VALUE) {
                    sample = Short.MAX_VALUE;
                } else if (sample < Short.MIN_VALUE) {
                    sample = Short.MIN_VALUE;
                }
                chunk[i] = (short) sample;
            }
        }

        return chunk;
    }

}
