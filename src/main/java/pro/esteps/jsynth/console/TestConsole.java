package pro.esteps.jsynth.console;

import pro.esteps.jsynth.frequency_generator.FixedFrequencyGenerator;
import pro.esteps.jsynth.wave_generator.SawWaveGenerator;
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

            FixedFrequencyGenerator fixedFrequencyGenerator = new FixedFrequencyGenerator();
            SawWaveGenerator generator = new SawWaveGenerator();
            fixedFrequencyGenerator.addConsumer(generator);

            FixedFrequencyGenerator fixedFrequencyGeneratorHalf = new FixedFrequencyGenerator();
            SawWaveGenerator generatorHalf = new SawWaveGenerator();
            fixedFrequencyGeneratorHalf.addConsumer(generatorHalf);

            Mixer mixer = new Mixer(2);
            mixer.setProducerForInput(0, generator, (byte) 100);
            mixer.setProducerForInput(1, generatorHalf, (byte) 50);

            Output output = new Output(mixer);
            Thread outputThread = new Thread(output);
            outputThread.start();

            String s;

            while (!(s = br.readLine()).equals("q")) {
                if (s.isEmpty()) {
                    fixedFrequencyGenerator.clearFrequency();
                    fixedFrequencyGeneratorHalf.clearFrequency();
                } else {
                    fixedFrequencyGenerator.setFrequency(Float.parseFloat(s));
                    fixedFrequencyGeneratorHalf.setFrequency(Float.parseFloat(s) / 2);
                }
            }

        }

    }

}