package pro.esteps.jsynth.websocket_api.input;

import pro.esteps.jsynth.messaging.message.Message;

/**
 * Drum machine message.
 * TODO: Add javadoc
 */
public class DrumMachineMessage extends Message {

    public DrumMachineMessage() {
    }

    protected DrumMachineMessage(String type) {
        super(type);
    }

    public static class Note {

        private boolean isEmpty;

        private String[] samples;

        public boolean isEmpty() {
            return isEmpty;
        }

        public String[] getSamples() {
            return samples;
        }

    }

    private int index;

    private Note[] sequence;

    public int getIndex() {
        return index;
    }

    public Note[] getSequence() {
        return sequence;
    }

}
