package pro.esteps.jsynth.synth_rack.synth;

import pro.esteps.jsynth.synth_rack.amplitude.SimpleDecay;
import pro.esteps.jsynth.synth_rack.contract.FrequencyConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.synth_rack.frequency_generator.FixedFrequencyGenerator;
import pro.esteps.jsynth.synth_rack.frequency_generator.FrequencyShift;
import pro.esteps.jsynth.synth_rack.mixer.EffectsProcessor;
import pro.esteps.jsynth.synth_rack.mixer.Mixer;
import pro.esteps.jsynth.synth_rack.sequencer.Note;
import pro.esteps.jsynth.synth_rack.sequencer.Sequencer;
import pro.esteps.jsynth.synth_rack.oscillator.Oscillator;

import java.util.ArrayList;
import java.util.List;

import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;

public class Synth implements FrequencyConsumer, SoundProducer {

    private Sequencer sequencer;

    private final FixedFrequencyGenerator frequencyGenerator;

    // todo Max 4 generators
    private final List<Oscillator> oscillators = new ArrayList<>();

    private SimpleDecay simpleDecay;

    private byte decayLength;

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
        outputMixer.setProducerForInput(0, effectsProcessor, (byte) 100);
    }

    public Synth(Sequencer.SequencerTempo tempo) {

        this.sequencer = new Sequencer(tempo);
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
        outputMixer.setProducerForInput(0, effectsProcessor, (byte) 100);

    }

    @Override
    public short[] getSoundChunk() {

        if (currentChunk == 1) {

            sequencer.advance();

            // todo Reset index only when a note changes
            /*
            if (simpleDecay != null) {
                simpleDecay.resetIndex();
                // simpleDecay.setDecayLength(sequencer.getCurrentDecayLength());
                simpleDecay.setDecayLength(decayLength);
            }
            */
            simpleDecay.resetIndex();
            simpleDecay.setDecayLength(decayLength);

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

    public void setGenerator(int index, Oscillator oscillator, float delta, byte volume) {
        assert index >= 0 && index < oscillators.size();
        if (index >= oscillators.size() - 1) {
            oscillators.add(index, oscillator);
        } else {
            oscillators.set(index, oscillator);
        }
        if (delta != 0) {
            FrequencyShift frequencyShift = new FrequencyShift(delta);
            frequencyShift.addConsumer((FrequencyConsumer) oscillator);
            frequencyGenerator.addConsumer(frequencyShift);
        } else {
            frequencyGenerator.addConsumer((FrequencyConsumer) oscillator);
        }
        generatorMixer.setProducerForInput(index, (SoundProducer) oscillators.get(index), volume);
    }

    public void setCutoffFrequency(float frequency) {
        this.effectsProcessor.setCutoffFrequency(frequency);
    }

    public void setResonance(byte resonance) {
        this.effectsProcessor.setResonance(resonance);
    }

    // todo Add delay parameters
    public void enableDelay() {
        this.effectsProcessor.enableDelay();
    }

    public void setDecayLength(byte decayLength) {
        this.decayLength = decayLength;
    }

    public void setTempo(Sequencer.SequencerTempo tempo) {
        this.sequencer.setTempo(tempo);
    }

}
