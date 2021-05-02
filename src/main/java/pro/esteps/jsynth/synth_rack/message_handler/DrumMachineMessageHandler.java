package pro.esteps.jsynth.synth_rack.message_handler;

import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.synth_rack.SynthRack;
import pro.esteps.jsynth.synth_rack.drum_machine.DrumMachine;
import pro.esteps.jsynth.synth_rack.sequencer.DrumMachineNote;
import pro.esteps.jsynth.synth_rack.synth.Synth;
import pro.esteps.jsynth.websocket_api.input.DrumMachineMessage;

/**
 * PubSub message handler for DrumMachineMessage.
 */
public class DrumMachineMessageHandler implements MessageHandler {

    private final SynthRack synthRack;

    public DrumMachineMessageHandler(SynthRack synthRack) {
        this.synthRack = synthRack;
    }

    @Override
    public void handleMessage(Message message) {

        if (!(message instanceof DrumMachineMessage)) {
            throw new IllegalArgumentException("Message has an illegal type: " + message.getClass());
        }
        var drumMachineMessage = (DrumMachineMessage) message;

        var drumMachine = synthRack.getDrumMachineByIndex(drumMachineMessage.getIndex());

        // Sequence
        DrumMachineNote[] notes = new DrumMachineNote[16];
        DrumMachineNote note;
        int i = 0;
        for (var inputNote : drumMachineMessage.getSequence()) {
            note = new DrumMachineNote(inputNote.getSamples());
            notes[i++] = note;
        }

        drumMachine.setSequence(notes);

    }
}
