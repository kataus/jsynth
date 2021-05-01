package pro.esteps.jsynth.api.input;

import pro.esteps.jsynth.pubsub.message.Message;

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

    public String getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public Note[] getSequence() {
        return sequence;
    }

}
