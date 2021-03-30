package pro.esteps.jsynth.generator;

public class SawGenerator implements Generator {

    private float frequency;

    /**
     * Cursor position within current period
     */
    private int currentPeriodCursorIndex;

    private byte[] period = new byte[0];

    private void regeneratePeriod() {

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

        if (currentPeriodCursorIndex > 0) {
            if (currentPeriodCursorIndex == currentPeriodSize - 1) {
                currentPeriodCursorIndex = newPeriodSize - 1;
            } else {
                currentPeriodCursorIndex = currentPeriodCursorIndex * newPeriodSize / currentPeriodSize;
            }
        }

    }

    @Override
    public byte[] generateChunk(float frequency) {

        if (this.frequency != frequency) {
            this.frequency = frequency;
            this.regeneratePeriod();
        }

        byte[] chunk = new byte[2048];
        for (int i = 0; i < chunk.length; i++) {
            chunk[i] = period[currentPeriodCursorIndex++];
            if (currentPeriodCursorIndex >= period.length) {
                currentPeriodCursorIndex = 0;
            }
        }

        return chunk;
    }

}
