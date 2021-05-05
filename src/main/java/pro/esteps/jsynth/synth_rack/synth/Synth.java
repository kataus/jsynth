package pro.esteps.jsynth.synth_rack.synth;

import pro.esteps.jsynth.synth_rack.amplitude.SimpleDecay;
import pro.esteps.jsynth.synth_rack.contract.FrequencyConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.synth_rack.frequency_generator.BypassFrequencyGenerator;
import pro.esteps.jsynth.synth_rack.frequency_generator.FrequencyShift;
import pro.esteps.jsynth.synth_rack.mixer.EffectsProcessor;
import pro.esteps.jsynth.synth_rack.mixer.Mixer;
import pro.esteps.jsynth.synth_rack.sequencer.Note;
import pro.esteps.jsynth.synth_rack.sequencer.Sequencer;
import pro.esteps.jsynth.synth_rack.oscillator.Oscillator;

import java.util.ArrayList;
import java.util.List;

import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;
import static pro.esteps.jsynth.synth_rack.config.Config.TICKS_PER_SEQUENCER_STEP;

/**
 * Synth.
 * <p>
 * TODO: Refactor this class
 */
public class Synth implements FrequencyConsumer, SoundProducer {

    private final Sequencer sequencer;

    private final BypassFrequencyGenerator frequencyGenerator;

    // TODO: Max 4 oscillators
    private final List<Oscillator> oscillators = new ArrayList<>();

    private final SimpleDecay simpleDecay;

    private byte decayLength;

    private final Mixer oscillatorMixer;

    private final EffectsProcessor effectsProcessor;

    private final Mixer outputMixer;

    private byte currentChunk = 1;

    // TODO: Get rid of this class and refactor the architecture
    private static class OscillatorSettings {

        private float delta;
        private byte volume;

        public float getDelta() {
            return delta;
        }

        public void setDelta(float delta) {
            this.delta = delta;
        }

        public byte getVolume() {
            return volume;
        }

        public void setVolume(byte volume) {
            this.volume = volume;
        }
    }

    // TODO: Get rid of this list and refactor the architecture
    private final List<OscillatorSettings> oscillatorSettingsList = new ArrayList<>();

    public Synth() {

        this.sequencer = new Sequencer();
        this.sequencer.setSequence(new Note[]{
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
        });

        this.frequencyGenerator = new BypassFrequencyGenerator();

        this.oscillatorMixer = new Mixer(4);

        this.simpleDecay = new SimpleDecay(oscillatorMixer, (byte) 1);

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

        this.frequencyGenerator = new BypassFrequencyGenerator();

        this.oscillatorMixer = new Mixer(4);

        this.simpleDecay = new SimpleDecay(oscillatorMixer, (byte) 1);

        this.effectsProcessor = new EffectsProcessor(simpleDecay);

        this.outputMixer = new Mixer(1);
        outputMixer.setProducerForInput(0, effectsProcessor, (byte) 100);

    }

    @Override
    public short[] getSoundChunk() {

        if (currentChunk == 1) {

            float previousFrequency = sequencer.getCurrentNoteFrequency();
            sequencer.advance();
            float frequency = sequencer.getCurrentNoteFrequency();

            if (decayLength == 0) {
                // If there's no decay, its index is always reset so the sound never stops
                simpleDecay.resetIndex();
            } else {
                // If there's a decay, it's index is reset only when the note changes
                if (frequency != 0 && frequency != previousFrequency) {
                    simpleDecay.resetIndex();
                }
            }
            simpleDecay.setDecayLength(decayLength);

            if (frequency == 0) {
                frequencyGenerator.clearFrequency();
            } else {
                frequencyGenerator.setFrequency(frequency);
            }

        }

        currentChunk++;
        if (currentChunk > TICKS_PER_SEQUENCER_STEP) {
            currentChunk = 1;
        }

        short[] chunk = new short[BUFFER_SIZE];
        System.arraycopy(outputMixer.getSoundChunk(), 0, chunk, 0, BUFFER_SIZE);

        return chunk;
    }

    /**
     * Set Synth frequency.
     *
     * @param frequency
     */
    public void setFrequency(float frequency) {
        assert frequency > 0;
        frequencyGenerator.setFrequency(frequency);
    }

    /**
     * Clear Synth frequency.
     */
    public void clearFrequency() {
        frequencyGenerator.clearFrequency();
    }

    /**
     * Set a new sequence.
     *
     * @param notes
     */
    public void setSequence(Note[] notes) {
        this.sequencer.setSequence(notes);
    }

    /**
     * Set an oscillator.
     *
     * @param index
     * @param oscillator
     * @param delta
     * @param volume
     */
    public void setOscillator(int index, Oscillator oscillator, float delta, byte volume) {
        assert index >= 0 && index < oscillators.size();

        if (index >= oscillators.size() - 1) {

            // Add new oscillator and connect it to the mixer
            oscillators.add(index, oscillator);

            // TODO: This is a duplicate code
            if (delta != 0) {
                FrequencyShift frequencyShift = new FrequencyShift(delta);
                frequencyShift.addConsumer((FrequencyConsumer) oscillator);
                frequencyGenerator.addConsumer(frequencyShift);
            } else {
                frequencyGenerator.addConsumer((FrequencyConsumer) oscillator);
            }
            oscillatorMixer.setProducerForInput(index, (SoundProducer) oscillators.get(index), volume);

            // TODO: Get rid of this code
            var oscillatorSettings = new OscillatorSettings();
            oscillatorSettings.setDelta(delta);
            oscillatorSettings.setVolume(volume);
            oscillatorSettingsList.add(index, oscillatorSettings);

        } else {

            Oscillator currentOscillator = oscillators.get(index);
            if (!currentOscillator.getClass().equals(oscillator.getClass())) {

                oscillators.set(index, oscillator);

                // TODO: This is a duplicate code
                if (delta != 0) {
                    FrequencyShift frequencyShift = new FrequencyShift(delta);
                    frequencyShift.addConsumer((FrequencyConsumer) oscillators.get(index));
                    frequencyGenerator.addConsumer(frequencyShift);
                } else {
                    frequencyGenerator.addConsumer((FrequencyConsumer) oscillators.get(index));
                }
                oscillatorMixer.setProducerForInput(index, (SoundProducer) oscillators.get(index), volume);

            }

            // TODO: Get rid of this code
            var oscillatorSettings = oscillatorSettingsList.get(index);

            if (delta != oscillatorSettings.getDelta() || volume != oscillatorSettings.getVolume()) {

                // TODO: This is a duplicate code
                if (delta != 0) {
                    FrequencyShift frequencyShift = new FrequencyShift(delta);
                    frequencyShift.addConsumer((FrequencyConsumer) oscillators.get(index));
                    frequencyGenerator.addConsumer(frequencyShift);
                } else {
                    frequencyGenerator.addConsumer((FrequencyConsumer) oscillators.get(index));
                }
                oscillatorMixer.setProducerForInput(index, (SoundProducer) oscillators.get(index), volume);

                oscillatorSettings.setDelta(delta);
                oscillatorSettings.setVolume(volume);

            }

        }
    }

    /**
     * Set the cutoff frequency.
     *
     * @param frequency
     */
    public void setCutoffFrequency(float frequency) {
        this.effectsProcessor.setCutoffFrequency(frequency);
    }

    /**
     * Set the resonance.
     *
     * @param resonance
     */
    public void setResonance(byte resonance) {
        this.effectsProcessor.setResonance(resonance);
    }

    /**
     * Enable the delay.
     * <p>
     * TODO: Add delay parameters
     */
    public void enableDelay() {
        this.effectsProcessor.enableDelay();
    }

    /**
     * Disable the delay.
     */
    public void disableDelay() {
        this.effectsProcessor.disableDelay();
    }

    /**
     * Set the decay length.
     *
     * @param decayLength
     */
    public void setDecayLength(byte decayLength) {
        this.decayLength = decayLength;
    }

}
