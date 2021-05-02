package pro.esteps.jsynth.synth_rack.sequencer;

/**
 * Drum machine Sequencer
 */
public class DrumMachineSequencer {

    private final DrumMachineNote[] sequence = new DrumMachineNote[16];

    private DrumMachineNote[] nextSequence;

    private DrumMachineNote currentNote;

    private int currentNoteIndex;

    private static final String[] EMPTY_NOTES = new String[0];

    public void setSequence(DrumMachineNote[] sequence) {
        // TODO: Add guard clauses
        this.nextSequence = sequence;
    }

    public void advance() {
        if (currentNoteIndex == 0) {
            if (nextSequence != null) {
                System.arraycopy(nextSequence, 0,sequence, 0, nextSequence.length);
                nextSequence = null;
            }
        }
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
