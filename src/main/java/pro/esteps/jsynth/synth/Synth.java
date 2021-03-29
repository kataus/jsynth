package pro.esteps.jsynth.synth;

import pro.esteps.jsynth.generator.Generator;
import pro.esteps.jsynth.generator.SawGenerator;
import pro.esteps.jsynth.state.State;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Synth implements Runnable {

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

    private final State state;

    public Synth(State state) {
        this.state = state;
    }

    public void run() {

        SourceDataLine soundLine = null;
        Generator generator = new SawGenerator();

        try {

            soundLine = AudioSystem.getSourceDataLine(FORMAT);
            final int bufferSize = 2048; // in Bytes
            final byte[] buffer = new byte[bufferSize];
            soundLine.open(FORMAT, bufferSize);
            soundLine.start();

            do {
                Float frequency = state.getFrequency();
                if (frequency == null) {
                    for (int i = 0; i < bufferSize; i++) {
                        buffer[i] = 0;
                    }
                } else {
                    byte[] chunk = generator.generateChunk(frequency);
                    System.arraycopy(chunk, 0, buffer, 0, bufferSize);
                }
                soundLine.write(buffer, 0, bufferSize);
            } while (true);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays a note.
     *
     * @param frequency
     */
    public void play(float frequency) {
        // todo
    }

    /**
     * Plays a rest (stops playing).
     */
    public void rest() {
        // todo
    }

}
