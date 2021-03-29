package pro.esteps.jsynth.output;

import pro.esteps.jsynth.state.MixerState;

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

    private MixerState mixerState;

    public Output(MixerState mixerState) {
        this.mixerState = mixerState;
    }

    public void run() {

        SourceDataLine soundLine = null;

        try {

            soundLine = AudioSystem.getSourceDataLine(FORMAT);
            final int bufferSize = 2048; // in Bytes
            final byte[] buffer = new byte[bufferSize];
            soundLine.open(FORMAT, bufferSize);
            soundLine.start();

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

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

}
