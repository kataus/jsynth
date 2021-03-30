package pro.esteps.jsynth.synth;

import pro.esteps.jsynth.generator.Generator;
import pro.esteps.jsynth.generator.SawGenerator;
import pro.esteps.jsynth.state.MixerState;
import pro.esteps.jsynth.state.State;

public class Synth implements Runnable {

    private final State state;
    private final MixerState mixerState;
    // todo Auto-calculate
    private final int channelIndex;

    public Synth(State state, MixerState mixerState, int channelIndex) {
        this.state = state;
        this.mixerState = mixerState;
        this.channelIndex = channelIndex;
    }

    public void run() {

        /*
        Generator generator = new SawGenerator();
        byte[] buffer = new byte[2048];

        do {
            Float frequency = state.getFrequency();
            if (frequency == null) {
                for (int i = 0; i < 2048; i++) {
                    buffer[i] = 0;
                }
            } else {
                byte[] chunk = generator.generateChunk(frequency);
                System.arraycopy(chunk, 0, buffer, 0, 2048);
            }
            mixerState.setChannelBuffer(channelIndex, buffer);
        } while (true);
        */

    }

}
