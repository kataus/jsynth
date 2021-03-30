package pro.esteps.jsynth.wave_generator;

import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.contract.SoundProducer;

import java.util.Arrays;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

// todo Add abstract parent
public class SquareWaveGenerator implements Generator, FrequencyConsumer, SoundProducer {

    private float frequency;

    /**
     * Cursor position within current period.
     */
    private int currentPeriodCursorIndex;

    private byte[] period = new byte[0];

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
            period = new byte[0];
            return;
        }

        int currentPeriodSize = period.length;
        int newPeriodSize = (int) (44100 / frequency);
        period = new byte[newPeriodSize];

        int fromIndex = 0;
        int toIndex = 0;
        byte sample = 127;

        toIndex = fromIndex + newPeriodSize / 2;
        Arrays.fill(period, fromIndex, toIndex, sample);
        fromIndex = toIndex;

        sample = -128;
        toIndex = newPeriodSize - 1;
        Arrays.fill(period, fromIndex, toIndex, sample);

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
    public byte[] getSoundChunk() {

        byte[] chunk = new byte[BUFFER_SIZE];

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
