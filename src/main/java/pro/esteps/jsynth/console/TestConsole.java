package pro.esteps.jsynth.console;

import pro.esteps.jsynth.output.Output;
import pro.esteps.jsynth.state.MixerState;
import pro.esteps.jsynth.state.State;
import pro.esteps.jsynth.synth.Synth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestConsole {

    /**
     * Process console input.
     *
     * @throws IOException
     */
    public void processConsoleInput() throws IOException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            // Shared state
            State state = new State();
            MixerState mixerState = new MixerState();

            // Output
            Output output = new Output(mixerState);
            Thread outputThread = new Thread(output);
            outputThread.start();

            // Synth
            Synth synth = new Synth(state, mixerState, 0);
            Thread synthThread = new Thread(synth);
            synthThread.start();

            String s;

            while (!(s = br.readLine()).equals("q")) {
                if (s.isEmpty()) {
                    state.clearFrequency();
                } else {
                    state.setFrequency(Float.parseFloat(s));
                }
            }

        }

    }

}