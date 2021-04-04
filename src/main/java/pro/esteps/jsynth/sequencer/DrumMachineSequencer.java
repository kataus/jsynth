package pro.esteps.jsynth.sequencer;

public class DrumMachineSequencer {

    private DrumMachineNote[] sequence = new DrumMachineNote[16];

    private DrumMachineNote currentNote;

    private int currentNoteIndex;

    private static final String[] EMPTY_NOTES = new String[0];

    public void setSequence(DrumMachineNote[] sequence) {
        // todo Guard clauses
        this.sequence = sequence;
    }

    public void advance() {
        DrumMachineNote note = sequence[currentNoteIndex++];
        if (currentNoteIndex >= sequence.length) {
            currentNoteIndex = 0;
        }
        currentNote = note;
    }

    public String[] getCurrentNoteSamples() {
        if (currentNote == null) {
            return EMPTY_NOTES;
        }
        return currentNote.getSamples();
    }

}
