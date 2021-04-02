package pro.esteps.jsynth.console;

import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.drum_machine.DrumMachine;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.parser.NoteParser;
import pro.esteps.jsynth.sequencer.DrumMachineSequencer;
import pro.esteps.jsynth.sequencer.Note;
import pro.esteps.jsynth.sequencer.Sequencer;
import pro.esteps.jsynth.sequencer.SynthNote;
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
            sequencer1.setSequence(new Note[]{

                    new SynthNote("c3", 800, (byte) 30),
                    new SynthNote("g3", 850, (byte) 30),
                    new SynthNote("c4", 900, (byte) 50),
                    new SynthNote("a#4", 1600, (byte) 80),

                    new SynthNote("g3", 1000, (byte) 50),
                    new SynthNote("c4", 1050, (byte) 30),
                    new SynthNote("c5", 1100, (byte) 30),
                    new SynthNote("g4", 1600, (byte) 30),

                    new SynthNote("c3", 1000, (byte) 30),
                    new SynthNote("g3", 950, (byte) 30),
                    new SynthNote("c4", 900, (byte) 50),
                    new SynthNote("a#4", 1600, (byte) 80),

                    new SynthNote("g3", 800, (byte) 50),
                    new SynthNote("c4", 800, (byte) 50),
                    new SynthNote("g4", 1600, (byte) 80),
                    new SynthNote("f4", 1600, (byte) 80),

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

            Synth synth1 = new Synth(800, 0);
            synth1.setGenerator1(new SawWaveGenerator());
            synth1.setSequencer(sequencer1);

            DrumMachine drumMachine = new DrumMachine();
            drumMachine.setSequencer(drumMachineSequencer);

            List<SoundProducer> synths = new ArrayList<>();

            synths.add(synth1);
            synths.add(drumMachine);

            Mixer mixer = new Mixer(3);
            mixer.setProducerForInput(0, synth1, (byte) 70);
            mixer.setProducerForInput(1, drumMachine, (byte) 70);

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