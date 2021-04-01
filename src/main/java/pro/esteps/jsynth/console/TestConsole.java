package pro.esteps.jsynth.console;

import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.drum_machine.DrumMachine;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.parser.NoteParser;
import pro.esteps.jsynth.sequencer.DrumMachineSequencer;
import pro.esteps.jsynth.sequencer.Sequencer;
import pro.esteps.jsynth.synth.Synth;
import pro.esteps.jsynth.output.Output;
import pro.esteps.jsynth.wave_generator.SawWaveGenerator;
import pro.esteps.jsynth.wave_generator.SineWaveGenerator;
import pro.esteps.jsynth.wave_generator.WhiteNoiseGenerator;

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

            Sequencer sequencer2 = new Sequencer();
            sequencer2.setSequence(new String[]{
                    "c2",
                    "",
                    "",
                    "",
                    "",
                    "c3",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "d#2",
                    "",
                    "",
                    "f2",
                    "g2",
            });

            Sequencer sequencer3 = new Sequencer();
            sequencer3.setSequence(new String[]{
                    "",
                    "",
                    "c2",
                    "",
                    "",
                    "",
                    "c2",
                    "",
                    "",
                    "",
                    "c2",
                    "",
                    "",
                    "",
                    "c2",
                    "",
            });

            DrumMachineSequencer drumMachineSequencer = new DrumMachineSequencer();
            drumMachineSequencer.setSequence(new String[][] {
                    {"kick", "hihat-closed"},
                    {"hihat-closed"},
                    {"hihat-closed"},
                    {"hihat-semiopen"},
                    {"snare", "hihat-closed"},
                    {"hihat-closed"},
                    {"hihat-closed"},
                    {"hihat-open"},
                    {"kick", "hihat-closed"},
                    {"hihat-semiopen"},
                    {"hihat-closed"},
                    {"hihat-closed"},
                    {"snare", "hihat-closed"},
                    {"hihat-closed"},
                    {"hihat-semiopen"},
                    {"hihat-open"},
            });

            // Synth synth1 = new Synth(600, true);
            Synth synth1 = new Synth(1200);

            synth1.setGenerator1(new SawWaveGenerator());
            // synth1.setGenerator2(new SquareWaveGenerator());
            synth1.setSequencer(sequencer1);

            Synth synth2 = new Synth(400);

            synth2.setGenerator1(new SineWaveGenerator());
            // synth2.setGenerator2(new SquareWaveGenerator());
            synth2.setSequencer(sequencer2);

            Synth synth3 = new Synth(4000);

            synth3.setGenerator1(new WhiteNoiseGenerator());
            synth3.setSequencer(sequencer3);

            DrumMachine drumMachine = new DrumMachine();
            drumMachine.setSequencer(drumMachineSequencer);

            List<SoundProducer> synths = new ArrayList<>();

            synths.add(synth1);
            synths.add(synth2);
            // synths.add(synth3);
            synths.add(drumMachine);

            Mixer mixer = new Mixer(3);
            mixer.setProducerForInput(0, synth1, (byte) 70);
            mixer.setProducerForInput(1, synth2, (byte) 70);
            // mixer.setProducerForInput(2, synth3, (byte) 70);
            mixer.setProducerForInput(2, drumMachine, (byte) 100);

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
                /*
                if (s.isEmpty()) {
                    synths.get(currentSynth).clearFrequency();
                    continue;
                }
                float frequency = noteParser.parseNote(s);
                synths.get(currentSynth).setFrequency(frequency);
                */
            }

        }

    }

}