package pro.esteps.jsynth.console;

import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.drum_machine.DrumMachine;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.parser.NoteParser;
import pro.esteps.jsynth.sequencer.*;
import pro.esteps.jsynth.synth.Synth;
import pro.esteps.jsynth.output.Output;
import pro.esteps.jsynth.wave_generator.*;

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

                    // --- 01 ---

                    new SynthNote("f4", 800, (byte) 30, (byte) 1),
                    new SynthNote("a2", 800, (byte) 30, (byte) 1),
                    new SynthNote("e3", 800, (byte) 30, (byte) 1),
                    new SynthNote("e4", 800, (byte) 30, (byte) 1),

                    new SynthNote("a2", 800, (byte) 30, (byte) 1),
                    new SynthNote("e3", 800, (byte) 30, (byte) 1),
                    new SynthNote("d4", 800, (byte) 30, (byte) 1),
                    new SynthNote("a2", 800, (byte) 30, (byte) 1),

                    new SynthNote("e3", 800, (byte) 30, (byte) 1),
                    new SynthNote("c4", 800, (byte) 30, (byte) 1),
                    new SynthNote("a2", 800, (byte) 30, (byte) 1),
                    new SynthNote("e3", 800, (byte) 30, (byte) 1),

                    new SynthNote("c4", 800, (byte) 30, (byte) 1),
                    new SynthNote("a3", 800, (byte) 30, (byte) 1),
                    new SynthNote("c4", 800, (byte) 30, (byte) 1),
                    new SynthNote("d4", 800, (byte) 30, (byte) 1),

            });

            Sequencer sequencer2 = new Sequencer(Sequencer.SequencerTempo.QUARTER);
            sequencer2.setSequence(new Note[]{

                    // --- 01 ---

                    new SynthNote("a2", 600, (byte) 0, (byte) 0),
                    null,
                    null,
                    null,

                    null,
                    null,
                    null,
                    null,

                    new SynthNote("d3", 600, (byte) 0, (byte) 0),
                    null,
                    null,
                    null,

                    new SynthNote("f2", 600, (byte) 0, (byte) 0),
                    null,
                    null,
                    null,

            });


            DrumMachineSequencer drumMachineSequencer = new DrumMachineSequencer();
            drumMachineSequencer.setSequence(new DrumMachineNote[] {

                    // --- 01 ---

                    new DrumMachineNote(new String[]{"kick", "hihat-closed"}),
                    new DrumMachineNote(new String[]{"hihat-semiopen"}),
                    null,
                    new DrumMachineNote(new String[]{"hihat-closed"}),

                    new DrumMachineNote(new String[]{"hihat-closed"}),
                    null,
                    new DrumMachineNote(new String[]{"hihat-semiopen"}),
                    null,

                    new DrumMachineNote(new String[]{"snare", "hihat-closed"}),
                    new DrumMachineNote(new String[]{"hihat-closed"}),
                    null,
                    new DrumMachineNote(new String[]{"hihat-closed"}),

                    new DrumMachineNote(new String[]{"hihat-semiopen"}),
                    null,
                    new DrumMachineNote(new String[]{"hihat-open"}),
                    null,

            });

            Synth synth1 = new Synth(800, 0);
            synth1.setGenerator1(new SawWaveGenerator(), 0);
            synth1.setGenerator2(new SquareWaveGenerator(), 1);
            synth1.setGenerator3(new SawWaveGenerator(), 101);
            synth1.setSequencer(sequencer1);

            Synth synth2 = new Synth(800);
            synth2.setGenerator1(new SquareWaveGenerator(), 0);
            synth2.setGenerator2(new SineWaveGenerator(), 1);
            // synth2.setGenerator3(new SineWaveGenerator(), -100);
            synth2.setSequencer(sequencer2);

            /*
            Synth synth3 = new Synth(800, 0);
            synth3.setGenerator1(new SawWaveGenerator(), 0);
            synth3.setGenerator2(new TriangleWaveGenerator(), 4);
            synth3.setGenerator3(new WhiteNoiseGenerator(), 0);
            // synth3.setGenerator2(new SineWaveGenerator(), 1);
            synth3.setSequencer(sequencer3);
            */

            DrumMachine drumMachine = new DrumMachine();
            drumMachine.setSequencer(drumMachineSequencer);

            /*
            List<SoundProducer> synths = new ArrayList<>();
            synths.add(synth1);
            synths.add(drumMachine);
            */

            Mixer mixer = new Mixer(4);
            mixer.setProducerForInput(0, synth1, (byte) 80);
            mixer.setProducerForInput(1, synth2, (byte) 80);
            // mixer.setProducerForInput(2, synth3, (byte) 80);
            mixer.setProducerForInput(3, drumMachine, (byte) 80);

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