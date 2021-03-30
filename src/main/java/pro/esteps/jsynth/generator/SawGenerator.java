package pro.esteps.jsynth.generator;

import pro.esteps.jsynth.contract.SoundProducer;

public class SawGenerator implements Generator, SoundProducer {

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

        int index = 0;
        byte sample;
        float divider = newPeriodSize / 256f;
        for (int i = newPeriodSize; i > 0; i--) {
            sample = (byte) (i / divider - 128);
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
    public byte[] getSoundChunk() {

        byte[] chunk = new byte[2048];

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
