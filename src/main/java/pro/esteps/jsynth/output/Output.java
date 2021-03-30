package pro.esteps.jsynth.output;

import pro.esteps.jsynth.contract.SoundProducer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Output implements Runnable {

    private static final int FRAME_SIZE = 1;

    private static final int SAMPLE_RATE = 44100;

    private static final AudioFormat FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            SAMPLE_RATE,
            8,
            1,
            FRAME_SIZE,
            SAMPLE_RATE,
            false
    );

    private final SoundProducer producer;

    public Output(SoundProducer producer) {
        this.producer = producer;
    }

    public void run() {

        SourceDataLine soundLine;

        try {

            soundLine = AudioSystem.getSourceDataLine(FORMAT);
            final int bufferSize = 2048; // in Bytes
            final byte[] buffer = new byte[bufferSize];
            soundLine.open(FORMAT, bufferSize);
            soundLine.start();

            /*
            float[] notes = new float[]{261.63f, 311.13f, 293.66f, 349.23f, 392.00f, 311.13f, 293.66f, 233.08f, 261.63f, 311.13f, 293.66f, 349.23f, 392.00f, 311.13f, 293.66f, 233.08f};
            Generator generator = new SawGenerator();
            for (float frequency : notes) {
                for (int k = 0; k < 5; k++) {
                    byte[] chunk = generator.generateChunk(frequency);
                    for (int i = 0; i < bufferSize; i++) {
                        buffer[i] = chunk[i];
                    }
                    soundLine.write(buffer, 0, bufferSize);
                }
            }
            */

            /*
            do {

                byte[][] channelBuffers = mixerState.getChannelBuffers();
                int sample;
                for (int sampleIndex = 0; sampleIndex < 2048; sampleIndex++) {
                    // Mix all channels
                    sample = 0;
                    for (int channelIndex = 0; channelIndex < 4; channelIndex++) {
                        sample += channelBuffers[channelIndex][sampleIndex];
                    }
                    // Normalize
                    if (sample > Byte.MAX_VALUE) {
                        sample = Byte.MAX_VALUE;
                    }
                    if (sample < Byte.MIN_VALUE) {
                        sample = Byte.MIN_VALUE;
                    }
                    // Add to buffer
                    buffer[sampleIndex] = (byte) sample;
                }

                soundLine.write(buffer, 0, bufferSize);

            } while (true);
            */

            do {
                System.arraycopy(producer.getSoundChunk(), 0, buffer, 0, 2048);
                soundLine.write(buffer, 0, 2048);
            } while (true);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

}
