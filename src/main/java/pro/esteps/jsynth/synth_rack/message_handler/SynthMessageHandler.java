package pro.esteps.jsynth.synth_rack.message_handler;

import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.synth_rack.SynthRack;
import pro.esteps.jsynth.synth_rack.oscillator.*;
import pro.esteps.jsynth.synth_rack.sequencer.EmptyNote;
import pro.esteps.jsynth.synth_rack.sequencer.Note;
import pro.esteps.jsynth.synth_rack.sequencer.SynthNote;
import pro.esteps.jsynth.synth_rack.synth.Synth;
import pro.esteps.jsynth.websocket_api.input.SynthMessage;

/**
 * PubSub message handler for SynthMessage.
 */
public class SynthMessageHandler implements MessageHandler {

    private final SynthRack synthRack;
    private SynthMessage message;
    private Synth synth;

    public SynthMessageHandler(SynthRack synthRack) {
        this.synthRack = synthRack;
    }

    @Override
    public void handleMessage(Message message) {

        if (!(message instanceof SynthMessage)) {
            throw new IllegalArgumentException("Message has an illegal type: " + message.getClass());
        }

        this.message = (SynthMessage) message;
        this.synth = synthRack.getSynthByIndex(this.message.getIndex());

        updateOscillators();
        updateSequence();
        updateEffects();
    }

    /**
     * Update oscillators.
     * <p>
     * TODO: Change existing settings instead of replacing them
     * TODO: Handle invalid values
     */
    private void updateOscillators() {

        var oscillatorMessages = message.getOscillators();
        for (int i = 0; i < 4; i++) {

            var oscillatorMessage = oscillatorMessages[i];

            Oscillator oscillator = switch (oscillatorMessage.getWaveform()) {
                case SAW -> new SawWaveOscillator();
                case SQUARE -> new SquareWaveOscillator();
                case SINE -> new SineWaveOscillator();
                case TRIANGLE -> new TriangleWaveOscillator();
            };

            synth.setOscillator(
                    i,
                    oscillator,
                    oscillatorMessage.getTune(),
                    (byte) oscillatorMessage.getVolume()
            );
        }

    }

    /**
     * Update sequence.
     * <p>
     * TODO: Change existing settings instead of replacing them
     */
    private void updateSequence() {

        Note[] notes = new Note[16];
        Note note;
        int i = 0;
        for (var inputNote : message.getSequence()) {
            note = null;
            if (inputNote.isEmpty()) {
                note = new EmptyNote();
            } else if (!inputNote.getNote().isEmpty()) {
                note = new SynthNote(inputNote.getNote());
            }
            notes[i++] = note;
        }

        synth.setSequence(notes);
    }

    /**
     * Update effects.
     * <p>
     * TODO: Change existing settings instead of replacing them
     */
    private void updateEffects() {

        synth.setCutoffFrequency(message.getCutoff());
        synth.setResonance((byte) message.getResonance());

        if (message.hasDecay()) {
            synth.setDecayLength((byte) 1);
        } else {
            synth.setDecayLength((byte) 0);
        }

        if (message.hasDelay()) {
            synth.enableDelay();
        } else {
            synth.disableDelay();
        }
    }

}
