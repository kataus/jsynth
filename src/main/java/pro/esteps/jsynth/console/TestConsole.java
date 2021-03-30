package pro.esteps.jsynth.console;

import pro.esteps.jsynth.generator.SawGenerator;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.output.Output;

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

            SawGenerator generator = new SawGenerator();

            Mixer mixer = new Mixer();
            mixer.addProducer(generator);

            Output output = new Output(mixer);
            Thread outputThread = new Thread(output);
            outputThread.start();

            String s;

            while (!(s = br.readLine()).equals("q")) {
                if (s.isEmpty()) {
                    generator.clearFrequency();
                } else {
                    generator.setFrequency(Float.parseFloat(s));
                }
            }

        }

    }

}