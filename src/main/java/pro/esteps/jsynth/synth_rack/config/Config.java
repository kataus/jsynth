package pro.esteps.jsynth.synth_rack.config;

import javax.sound.sampled.AudioFormat;

public interface Config {

    public static final int SAMPLE_RATE = 44100;

    public static final int BUFFER_SIZE = 1024;

    public static final int TICKS_PER_SEQUENCER_STEP = 5;

    public static final AudioFormat FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            SAMPLE_RATE,
            16,
            1,
            2,
            SAMPLE_RATE,
            false
    );

}
