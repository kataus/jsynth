package pro.esteps.jsynth.synth;

import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.frequency_generator.FixedFrequencyGenerator;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.wave_generator.Generator;
import pro.esteps.jsynth.wave_generator.SawWaveGenerator;
import pro.esteps.jsynth.wave_generator.SquareWaveGenerator;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class Synth implements SoundProducer {

    private FixedFrequencyGenerator frequencyGenerator;

    private Generator generator1;

    private Generator generator2;

    private Mixer generatorMixer;

    public Synth() {

        this.frequencyGenerator = new FixedFrequencyGenerator();
        this.generator1 = new SawWaveGenerator();
        this.generator2 = new SquareWaveGenerator();
        this.generatorMixer = new Mixer(2);

        frequencyGenerator.addConsumer((FrequencyConsumer) generator1);
        frequencyGenerator.addConsumer((FrequencyConsumer) generator2);

        generatorMixer.setProducerForInput(0, (SoundProducer) generator1, (byte) 100);
        generatorMixer.setProducerForInput(1, (SoundProducer) generator2, (byte) 100);

    }

    @Override
    public byte[] getSoundChunk() {
        byte[] chunk = new byte[BUFFER_SIZE];
        System.arraycopy(generatorMixer.getSoundChunk(), 0, chunk, 0, BUFFER_SIZE);
        return chunk;
    }

    public void setFrequency(float frequency) {
        assert frequency > 0;
        this.frequencyGenerator.setFrequency(frequency);
    }

    public void clearFrequency() {
        this.frequencyGenerator.clearFrequency();
    }

}
