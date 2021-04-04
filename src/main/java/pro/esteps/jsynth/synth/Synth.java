package pro.esteps.jsynth.synth;

import pro.esteps.jsynth.amplitude.SimpleDecay;
import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.frequency_generator.FixedFrequencyGenerator;
import pro.esteps.jsynth.frequency_generator.FrequencyShift;
import pro.esteps.jsynth.fx.*;
import pro.esteps.jsynth.mixer.EffectsProcessor;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.sequencer.Note;
import pro.esteps.jsynth.sequencer.Sequencer;
import pro.esteps.jsynth.sequencer.SynthNote;
import pro.esteps.jsynth.wave_generator.Generator;

import java.util.ArrayList;
import java.util.List;

import static pro.esteps.jsynth.App.BUFFER_SIZE;

public class Synth implements FrequencyConsumer, SoundProducer {

    private Sequencer sequencer;

    private final FixedFrequencyGenerator frequencyGenerator;

    // todo Max 4 generators
    private final List<Generator> generators = new ArrayList<>();

    private SimpleDecay simpleDecay;

    private final Mixer generatorMixer;

    private final EffectsProcessor effectsProcessor;

    private final Mixer outputMixer;

    // todo Duplicate code
    private static final int CHUNKS_PER_NOTE = 5;

    // todo Duplicate code
    private byte currentChunk = 1;

    public Synth() {

        this.sequencer = new Sequencer();
        this.sequencer.setSequence(new Note[]{
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
        });

        this.frequencyGenerator = new FixedFrequencyGenerator();

        this.generatorMixer = new Mixer(4);

        this.simpleDecay = new SimpleDecay(generatorMixer, (byte) 1);

        this.effectsProcessor = new EffectsProcessor(simpleDecay);

        this.outputMixer = new Mixer(1);
        outputMixer.setProducerForInput(0, simpleDecay, (byte) 100);
    }

    @Override
    public short[] getSoundChunk() {

        if (currentChunk == 1) {

            sequencer.advance();

            // todo Reset index only when a note changes
            if (simpleDecay != null) {
                simpleDecay.resetIndex();
                // simpleDecay.setDecayLength(sequencer.getCurrentDecayLength());
                simpleDecay.setDecayLength((byte) 1);
            }

            float frequency = sequencer.getCurrentNoteFrequency();
            if (frequency == 0) {
                frequencyGenerator.clearFrequency();
            } else {
                frequencyGenerator.setFrequency(frequency);
            }

            /*
            this.lowPassFilter.setFrequency(sequencer.getCurrentNoteLowPassFilterFrequency());
            this.lowPassFilter.setResonanceAmount(sequencer.getCurrentNoteLowPassFilterResonance());
            */
        }

        currentChunk++;
        if (currentChunk > CHUNKS_PER_NOTE) {
            currentChunk = 1;
        }

        short[] chunk = new short[BUFFER_SIZE];
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

    public void setSequence(Note[] notes) {
        this.sequencer.setSequence(notes);
    }

    public void setGenerator(int index, Generator generator, float delta, byte volume) {
        assert index >= 0 && index < generators.size();
        if (index >= generators.size() - 1) {
            generators.add(index, generator);
        } else {
            generators.set(index, generator);
        }
        if (delta != 0) {
            FrequencyShift frequencyShift = new FrequencyShift(delta);
            frequencyShift.addConsumer((FrequencyConsumer) generator);
            frequencyGenerator.addConsumer(frequencyShift);
        } else {
            frequencyGenerator.addConsumer((FrequencyConsumer) generator);
        }
        generatorMixer.setProducerForInput(index, (SoundProducer) generators.get(index), volume);
    }

}
