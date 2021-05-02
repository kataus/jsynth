package pro.esteps.jsynth.websocket_api.output;

import pro.esteps.jsynth.messaging.message.Message;

/**
 * This message is sent on every sequencer step.
 */
public class SequencerStepMessage extends Message {

    private int sequencerStep;

    public SequencerStepMessage(int sequencerStep) {
        super("sequencerStep");
        this.sequencerStep = sequencerStep;
    }

    public int getSequencerStep() {
        return sequencerStep;
    }

}
