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

            // Init synths

            List<Synth> synths = new ArrayList<>();

            for (byte i = 0; i < 4; i++) {
                synths.add(i, new Synth());
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

            /*
            mixer.setProducerForInput(4, drumMachines.get(0), (byte) 80);
            mixer.setProducerForInput(5, drumMachines.get(1), (byte) 80);
            */

            // Start mixer in a separate thread

            Output output = new Output(mixer);
            Thread outputThread = new Thread(output);
            outputThread.start();

            // Command processing

            String s;

            while (!(s = br.readLine()).equals("quit")) {

            }

        }

    }

}