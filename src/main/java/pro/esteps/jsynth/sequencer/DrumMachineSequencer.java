package pro.esteps.jsynth.sequencer;

public class DrumMachineSequencer {

    private String[][] sequence = new String[16][4];

    private int currentNote;

    public void setSequence(String[][] sequence) {
        this.sequence = sequence;
    }

    public String[] getNextNotes() {
        String[] notes = sequence[currentNote++];
        System.out.println(notes);
        if (currentNote >= sequence.length) {
            currentNote = 0;
        }
        return notes;
    }

}
