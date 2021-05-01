package pro.esteps.jsynth.console;

import pro.esteps.jsynth.api.server.SynthServer;
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
import java.util.Arrays;
import java.util.List;

public class TestConsole {

    private NoteParser noteParser;
    private SynthServer webSocketServer;

    public TestConsole(SynthServer webSocketServer) {
        this.noteParser = new NoteParser();
        this.webSocketServer = webSocketServer;
    }

    /**
     * Process console input.
     *
     * @throws IOException
     */
    public void processConsoleInput() throws IOException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            // Init synths

            List<Synth> synths = new ArrayList<>();

            for (byte i = 0; i < 4; i++) {
                if (i == 0 || i == 3) {
                    // todo Sync tempo bet
                    synths.add(i, new Synth(Sequencer.SequencerTempo.QUARTER));
                } else {
                    synths.add(i, new Synth());
                }
            }

            // Init drum machines

            List<DrumMachine> drumMachines = new ArrayList<>();

            for (byte i = 0; i < 2; i++) {
                drumMachines.add(i, new DrumMachine());
            }

            // Init mixer

            Mixer mixer = new Mixer(6);

            mixer.setProducerForInput(0, synths.get(0), (byte) 80);
            mixer.setProducerForInput(1, synths.get(1), (byte) 80);
            mixer.setProducerForInput(2, synths.get(2), (byte) 80);
            mixer.setProducerForInput(3, synths.get(3), (byte) 80);

            // todo Add volume control
            mixer.setProducerForInput(4, drumMachines.get(0), (byte) 80);
            // todo Add volume control
            mixer.setProducerForInput(5, drumMachines.get(1), (byte) 50);

            // Start mixer in a separate thread

            Output output = new Output(mixer, webSocketServer);
            Thread outputThread = new Thread(output);
            outputThread.start();

            // Command processing

            String str;

            String synth;
            String action;

            Synth currentSynth = null;
            DrumMachine currentDrumMachine = null;

            while (!(str = br.readLine()).equals("quit")) {

                // todo Parser with validation
                String[] splitted = str.split("\\s+");

                synth = splitted[0];
                action = splitted[1];

                // Synths
                // todo Refactor to separate classes/methods

                if (synth.equals("s1") || synth.equals("s2") || synth.equals("s3") || synth.equals("s4")) {

                    int index = Integer.parseInt(synth.substring(1)) - 1;
                    currentSynth = synths.get(index);
                    System.out.println("Current synth: " + index);

                    if (action.equals("sequence") && currentSynth != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        Note[] notes = parseSequence(splitted[2]);
                        currentSynth.setSequence(notes);
                        System.out.println("Set new sequence: " + Arrays.toString(notes));
                    }

                    if (action.equals("generator") && currentSynth != null) {

                        if (splitted.length != 6) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }

                        Generator generator = null;
                        int generatorIndex = Integer.parseInt(splitted[2]);
                        String generatorType = splitted[3];
                        float frequencyDelta = Float.parseFloat(splitted[4]);
                        byte volume = Byte.parseByte(splitted[5]);

                        if (generatorType.equals("saw")) {
                            generator = new SawWaveGenerator();
                        }
                        if (generatorType.equals("square")) {
                            generator = new SquareWaveGenerator();
                        }
                        if (generatorType.equals("sine")) {
                            generator = new SineWaveGenerator();
                        }
                        if (generatorType.equals("triangle")) {
                            generator = new TriangleWaveGenerator();
                        }
                        if (generatorType.equals("white")) {
                            generator = new WhiteNoiseGenerator();
                        }

                        currentSynth.setGenerator(
                                generatorIndex,
                                generator,
                                frequencyDelta,
                                volume
                        );

                        System.out.println("Set new generator");
                    }

                    if (action.equals("cutoff") && currentSynth != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        float frequency = Float.parseFloat(splitted[2]);
                        currentSynth.setCutoffFrequency(frequency);
                        System.out.println("Cutoff set: " + frequency);
                    }

                    if (action.equals("resonance") && currentSynth != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        byte resonance = Byte.parseByte(splitted[2]);
                        currentSynth.setResonance(resonance);
                        System.out.println("Resonance set: " + resonance);
                    }

                    if (action.equals("decay") && currentSynth != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        byte decayLength = Byte.parseByte(splitted[2]);
                        currentSynth.setDecayLength(decayLength);
                        System.out.println("Decay set: " + decayLength);
                    }

                    if (action.equals("delay") && currentSynth != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        if (splitted[2].equals("1")) {
                            currentSynth.enableDelay();
                            System.out.println("Delay enabled");
                        }
                    }

                    if (action.equals("tempo") && currentSynth != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        Sequencer.SequencerTempo tempo = null;
                        if (splitted[2].equals("one")) {
                            tempo = Sequencer.SequencerTempo.ONE;
                        }
                        if (splitted[2].equals("half")) {
                            tempo = Sequencer.SequencerTempo.HALF;
                        }
                        if (splitted[2].equals("quarter")) {
                            tempo = Sequencer.SequencerTempo.QUARTER;
                        }
                        currentSynth.setTempo(tempo);
                    }

                }

                // Drum Machines
                // todo Refactor to separate classes/methods

                if (synth.equals("d1") || synth.equals("d2")) {

                    int index = Integer.parseInt(synth.substring(1)) - 1;
                    currentDrumMachine = drumMachines.get(index);
                    System.out.println("Current drum machine: " + index);

                    if (action.equals("sequence") && currentDrumMachine != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        DrumMachineNote[] notes = parseDrumSequence(splitted[2]);
                        currentDrumMachine.setSequence(notes);
                        System.out.println("Set new sequence: " + Arrays.toString(notes));
                    }

                    if (action.equals("delay") && currentDrumMachine != null) {
                        if (splitted.length != 3) {
                            System.out.println("Parameters are not specified");
                            continue;
                        }
                        if (splitted[2].equals("1")) {
                            currentDrumMachine.enableDelay();
                            System.out.println("Delay enabled");
                        }
                    }

                }

            }

            outputThread.interrupt();

        }

    }

    // todo Refactor to a separate class
    // todo Validate input
    // todo Move magic number 16 to a constant
    private Note[] parseSequence(String parameters) {
        Note[] notes = new Note[16];
        String[] splitted = parameters.split(",");
        String str;
        Note note;
        for (int i = 0; i < 16; i++) {
            note = null;
            if (splitted.length > i) {
                str = splitted[i];
                if (str.equals(".")) {
                    note = new EmptyNote();
                } else if (!str.isEmpty()) {
                    note = new SynthNote(str);
                }
            }
            notes[i] = note;
        }
        return notes;
    }

    private DrumMachineNote[] parseDrumSequence(String parameters) {
        DrumMachineNote[] notes = new DrumMachineNote[16];
        String[] splitted = parameters.split(",");
        String str;
        DrumMachineNote note;
        for (int i = 0; i < 16; i++) {
            note = null;
            if (splitted.length > i) {
                str = splitted[i];
                if (!str.isEmpty()) {
                    String[] splittedSamples = str.split("\\|");
                    note = new DrumMachineNote(splittedSamples);
                }

            }
            notes[i] = note;
        }
        return notes;
    }

}