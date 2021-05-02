package pro.esteps.jsynth.synth_rack.oscillator;

import pro.esteps.jsynth.synth_rack.contract.FrequencyConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

// todo Add abstract parent
public class SawWaveOscillator implements Oscillator, FrequencyConsumer, SoundProducer {

    private float frequency;

    /**
     * Cursor position within current period.
     */
    private int currentPeriodCursorIndex;

    private short[] period = new short[0];

    public void setFrequency(float frequency) {
        assert frequency > 0;
        if (this.frequency != frequency) {
            this.frequency = frequency;
            this.regeneratePeriod();
        }
    }

    public void clearFrequency() {
        this.frequency = 0;
        this.regeneratePeriod();
    }

    private void regeneratePeriod() {

        if (frequency == 0) {
            period = new short[0];
            return;
        }

        int currentPeriodSize = period.length;
        int newPeriodSize = (int) (SAMPLE_RATE / frequency);
        period = new short[newPeriodSize];

        int index = 0;
        short sample;
        float divider = newPeriodSize / 65536f;
        for (int i = newPeriodSize; i > 0; i--) {
            sample = (short) (i / divider - 32768);
            period[index++] = sample;
        }

        if (currentPeriodSize == 0) {
            currentPeriodCursorIndex = 0;
        } else if (currentPeriodCursorIndex > 0) {
            if (currentPeriodCursorIndex == currentPeriodSize - 1) {
                currentPeriodCursorIndex = newPeriodSize - 1;
            } else {
                currentPeriodCursorIndex = currentPeriodCursorIndex * newPeriodSize / currentPeriodSize;
            }
        }

    }

    @Override
    public short[] getSoundChunk() {

        short[] chunk = new short[BUFFER_SIZE];

        if (period.length == 0) {
            return chunk;
        }

        for (int i = 0; i < chunk.length; i++) {
            chunk[i] = period[currentPeriodCursorIndex++];
            if (currentPeriodCursorIndex >= period.length) {
                currentPeriodCursorIndex = 0;
            }
        }

        return chunk;

    }

}
