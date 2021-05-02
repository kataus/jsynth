package pro.esteps.jsynth.synth_rack.message_handler;

import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.synth_rack.SynthRack;
import pro.esteps.jsynth.synth_rack.oscillator.*;
import pro.esteps.jsynth.synth_rack.sequencer.EmptyNote;
import pro.esteps.jsynth.synth_rack.sequencer.Note;
import pro.esteps.jsynth.synth_rack.sequencer.SynthNote;
import pro.esteps.jsynth.websocket_api.input.SynthMessage;

/**
 * PubSub message handler for SynthMessage.
 */
public class SynthMessageHandler implements MessageHandler {

    private final SynthRack synthRack;

    public SynthMessageHandler(SynthRack synthRack) {
        this.synthRack = synthRack;
    }

    @Override
    public void handleMessage(Message message) {

        if (!(message instanceof SynthMessage)) {
            throw new IllegalArgumentException("Message has an illegal type: " + message.getClass());
        }
        var synthMessage = (SynthMessage) message;

        var synth = synthRack.getSynthByIndex(synthMessage.getIndex());

        // Oscillators
        // todo Change existing generator settings instead of replacing it

        var oscillatorMessages = synthMessage.getOscillators();
        for (int i = 0; i < 4; i++) {

            Oscillator oscillator = null;

            var oscillatorMessage = oscillatorMessages[i];
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SAW) {
                oscillator = new SawWaveOscillator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SQUARE) {
                oscillator = new SquareWaveOscillator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SINE) {
                oscillator = new SineWaveOscillator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.TRIANGLE) {
                oscillator = new TriangleWaveOscillator();
            }
            // todo Handle other (i.e. missing or invalid) values

            synth.setGenerator(
                    i,
                    oscillator,
                    oscillatorMessage.getTune(),
                    (byte) oscillatorMessage.getVolume()
            );
        }

        // Sequence

        Note[] notes = new Note[16];
        Note note;
        int i = 0;
        for (var inputNote : synthMessage.getSequence()) {
            note = null;
            if (inputNote.isEmpty()) {
                note = new EmptyNote();
            } else if (!inputNote.getNote().isEmpty()) {
                note = new SynthNote(inputNote.getNote());
            }
            notes[i++] = note;
        }

        synth.setSequence(notes);

        // Effects
        // todo Change existing values instead of replacing them

        synth.setCutoffFrequency(synthMessage.getCutoff());

        synth.setResonance((byte) synthMessage.getResonance());

        if (synthMessage.hasDecay()) {
            synth.setDecayLength((byte) 1);
        } else {
            synth.setDecayLength((byte) 0);
        }

        // todo Disable delay
        if (synthMessage.hasDelay()) {
            synth.enableDelay();
        }


    }
}
