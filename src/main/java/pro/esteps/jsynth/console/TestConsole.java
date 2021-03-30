package pro.esteps.jsynth.console;

import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.parser.NoteParser;
import pro.esteps.jsynth.sequencer.Sequencer;
import pro.esteps.jsynth.synth.Synth;
import pro.esteps.jsynth.output.Output;
import pro.esteps.jsynth.wave_generator.SawWaveGenerator;
import pro.esteps.jsynth.wave_generator.SquareWaveGenerator;

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

            Sequencer sequencer1 = new Sequencer();
            sequencer1.setSequence(new String[]{
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

            Synth synth1 = new Synth();

            synth1.setGenerator1(new SawWaveGenerator());
            synth1.setGenerator2(new SquareWaveGenerator());
            synth1.setSequencer(sequencer1);

            List<Synth> synths = new ArrayList<>();

            synths.add(synth1);

            Mixer mixer = new Mixer(2);
            mixer.setProducerForInput(0, synth1, (byte) 50);

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