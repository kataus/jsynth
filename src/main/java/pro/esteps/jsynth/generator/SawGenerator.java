package pro.esteps.jsynth.generator;

public class SawGenerator implements Generator {

    @Override
    public byte[] generateChunk(float frequency) {
        byte[] chunk = new byte[2048];
        int segmentSize = (int) (44100 / frequency);
        int fromIndex = 0;
        byte sample;
        do {
            float divider = segmentSize / 256f;
            for (int i = segmentSize; i > 0; i--) {
                sample = (byte) (i / divider - 128);
                chunk[fromIndex++] = sample;
                if (fromIndex == chunk.length) {
                    break;
                }
            }
            if (fromIndex == chunk.length) {
                break;
            }
        } while (true);
        return chunk;
    }

}
