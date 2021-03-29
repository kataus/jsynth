package pro.esteps.jsynth.console;

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

            State state = new State();

            Synth synth = new Synth(state);
            Thread t = new Thread(synth);
            t.start();

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