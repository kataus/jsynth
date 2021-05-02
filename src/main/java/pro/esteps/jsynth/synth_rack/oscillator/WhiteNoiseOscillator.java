package pro.esteps.jsynth.synth_rack.oscillator;

import pro.esteps.jsynth.synth_rack.contract.FrequencyConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class WhiteNoiseOscillator implements Oscillator, FrequencyConsumer, SoundProducer {

    private float frequency;

    private short[] period = new short[0];

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
        period = new short[BUFFER_SIZE];
        for (int i = 0; i < BUFFER_SIZE; i++) {
            period[i] = (short) ((Math.random() * 65536 - 32768));
        }
    }

    @Override
    public short[] getSoundChunk() {

        short[] chunk = new short[BUFFER_SIZE];

        if (frequency == 0) {
            return chunk;
        }

        regeneratePeriod();
        System.arraycopy(period, 0, chunk, 0, BUFFER_SIZE);

        return chunk;
    }

}
