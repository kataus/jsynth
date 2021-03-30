package pro.esteps.jsynth.wave_generator;

import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class WhiteNoiseGenerator implements Generator, FrequencyConsumer, SoundProducer {

    private float frequency;

    private byte[] period = new byte[0];

    // FrequencyProducer works as an off/on switch
    public void setFrequency(float frequency) {
        assert frequency > 0;
        if (this.frequency != frequency) {
            this.frequency = frequency;
        }
    }

    public void clearFrequency() {
        this.frequency = 0;
    }

    private void regeneratePeriod() {
        period = new byte[BUFFER_SIZE];
        for (int i = 0; i < BUFFER_SIZE; i++) {
            period[i] = (byte) ((Math.random() * 255 - 128));
        }
    }

    @Override
    public byte[] getSoundChunk() {

        byte[] chunk = new byte[BUFFER_SIZE];

        if (frequency == 0) {
            return chunk;
        }

        regeneratePeriod();
        System.arraycopy(period, 0, chunk, 0, BUFFER_SIZE);

        return chunk;
    }

}