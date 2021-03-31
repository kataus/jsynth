package pro.esteps.jsynth.synth;

import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.frequency_generator.FixedFrequencyGenerator;
import pro.esteps.jsynth.fx.Distortion;
import pro.esteps.jsynth.fx.FixedDelay;
import pro.esteps.jsynth.fx.LowHighPassFilter;
import pro.esteps.jsynth.fx.LowPassFilter;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.sequencer.Sequencer;
import pro.esteps.jsynth.wave_generator.Generator;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class Synth implements FrequencyConsumer, SoundProducer {

    private FixedFrequencyGenerator frequencyGenerator;

    private Generator generator1;

    private Generator generator2;

    private Mixer generatorMixer;

    private Mixer outputMixer;

    private FixedDelay fixedDelay;

    private Distortion distortion;

    private LowHighPassFilter lowHighPassFilter;

    private LowPassFilter lowPassFilter;

    private Sequencer sequencer;

    private static final int CHUNKS_PER_NOTE = 5;

    private int currentChunk = 1;

    // todo Remove overloaded constructor
    public Synth(int frequency) {
        this.frequencyGenerator = new FixedFrequencyGenerator();
        this.generatorMixer = new Mixer(2);
        /*
        this.lowHighPassFilter = new LowHighPassFilter(
                generatorMixer,
                frequency,
                LowHighPassFilter.PassType.Lowpass,
                1f
        );
        */
        this.lowPassFilter = new LowPassFilter(generatorMixer, frequency);
        this.fixedDelay = new FixedDelay(lowPassFilter);
        // this.fixedDelay = new FixedDelay(generatorMixer);
        this.outputMixer = new Mixer(1);
        outputMixer.setProducerForInput(0, (SoundProducer) fixedDelay, (byte) 100);
    }

    // todo Remove overloaded constructor
    public Synth(int frequency, boolean hasDistortion) {
        this.frequencyGenerator = new FixedFrequencyGenerator();
        this.generatorMixer = new Mixer(2);
        this.distortion = new Distortion(generatorMixer);
        /*
        this.lowHighPassFilter = new LowHighPassFilter(
                distortion,
                frequency,
                LowHighPassFilter.PassType.Lowpass,
                1f
        );
        */
        // this.lowPassFilter = new LowPassFilter(generatorMixer, frequency);
        // this.fixedDelay = new FixedDelay(lowPassFilter);
        this.fixedDelay = new FixedDelay(generatorMixer);
        this.outputMixer = new Mixer(1);
        outputMixer.setProducerForInput(0, (SoundProducer) fixedDelay, (byte) 100);
    }

    // todo Remove overloaded constructor
    public Synth() {
        this.frequencyGenerator = new FixedFrequencyGenerator();
        this.generatorMixer = new Mixer(2);
        this.fixedDelay = new FixedDelay(generatorMixer);
        this.outputMixer = new Mixer(1);
        outputMixer.setProducerForInput(0, (SoundProducer) fixedDelay, (byte) 100);
    }

    public void setGenerator1(Generator generator) {
        this.generator1 = generator;
        // todo Set consumer using index
        frequencyGenerator.addConsumer((FrequencyConsumer) generator1);
        generatorMixer.setProducerForInput(0, (SoundProducer) generator1, (byte) 50);
    }

    public void setGenerator2(Generator generator) {
        this.generator2 = generator;
        // todo Set consumer using index
        frequencyGenerator.addConsumer((FrequencyConsumer) generator2);
        generatorMixer.setProducerForInput(1, (SoundProducer) generator2, (byte) 25);
    }

    public void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    @Override
    public short[] getSoundChunk() {

        if (currentChunk == 1) {
            float frequency = sequencer.getNextNoteFrequency();
            if (frequency == 0) {
                frequencyGenerator.clearFrequency();
            } else {
                frequencyGenerator.setFrequency(frequency);
            }
        }
        currentChunk++;
        if (currentChunk > CHUNKS_PER_NOTE) {
            currentChunk = 1;
        }

        short[] chunk = new short[BUFFER_SIZE];
        // todo Use FX Mixer
        System.arraycopy(outputMixer.getSoundChunk(), 0, chunk, 0, BUFFER_SIZE);
        return chunk;
    }

    public void setFrequency(float frequency) {
        assert frequency > 0;
        frequencyGenerator.setFrequency(frequency);
    }

    public void clearFrequency() {
        frequencyGenerator.clearFrequency();
    }

}
