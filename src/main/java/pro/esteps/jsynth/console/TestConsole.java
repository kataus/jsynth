package pro.esteps.jsynth.console;

import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.parser.NoteParser;
import pro.esteps.jsynth.sequencer.Sequencer;
import pro.esteps.jsynth.synth.Synth;
import pro.esteps.jsynth.output.Output;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestConsole {

    private NoteParser noteParser;

    public TestConsole() {
        this.noteParser = new NoteParser();
    }

    /**
     * Process console input.
     *
     * @throws IOException
     */
    public void processConsoleInput() throws IOException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            Synth synth1 = new Synth();
            Synth synth2 = new Synth();

            List<Synth> synths = new ArrayList<>();

            synths.add(synth1);
            synths.add(synth2);

            Mixer mixer = new Mixer(2);
            mixer.setProducerForInput(0, synth1, (byte) 100);
            mixer.setProducerForInput(1, synth2, (byte) 100);

            Sequencer sequencer = new Sequencer();
            sequencer.setFrequencyConsumer(synth2);
            sequencer.setSequence(new String[]{
                    "c3",
                    "g3",
                    "c4",
                    "a#4",
                    "g3",
                    "c4",
                    "c5",
                    "g4",
                    "c3",
                    "g3",
                    "c4",
                    "a#4",
                    "g3",
                    "c4",
                    "g4",
                    "f4"
            });
            Thread sequencerThread = new Thread(sequencer);
            sequencerThread.start();

            Output output = new Output(mixer);
            Thread outputThread = new Thread(output);
            outputThread.start();

            String s;
            int currentSynth = 0;

            while (!(s = br.readLine()).equals("quit")) {
                if (s.equals("s1")) {
                    currentSynth = 0;
                    System.out.println("Current synth: S1");
                    continue;
                }
                if (s.equals("s2")) {
                    currentSynth = 1;
                    System.out.println("Current synth: S2");
                    continue;
                }
                if (s.isEmpty()) {
                    synths.get(currentSynth).clearFrequency();
                    continue;
                }
                float frequency = noteParser.parseNote(s);
                synths.get(currentSynth).setFrequency(frequency);
            }

        }

    }

}