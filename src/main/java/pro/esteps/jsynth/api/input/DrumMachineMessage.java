package pro.esteps.jsynth.api.input;

import pro.esteps.jsynth.pubsub.message.Message;

public class DrumMachineMessage implements Message {

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

    // todo Use enum
    private String type;
    private String drumMachine;
    private Note sequence;

    public String getType() {
        return type;
    }

    public String getDrumMachine() {
        return drumMachine;
    }

    public Note getSequence() {
        return sequence;
    }

}
