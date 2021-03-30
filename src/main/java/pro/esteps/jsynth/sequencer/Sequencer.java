package pro.esteps.jsynth.sequencer;

import pro.esteps.jsynth.parser.NoteParser;

public class Sequencer {

    private String[] sequence = new String[16];

    private NoteParser noteParser;

    private int currentNote;

    public void setSequence(String[] sequence) {
        this.sequence = sequence;
    }

    public Sequencer() {
        this.noteParser = new NoteParser();
    }

    public float getNextNoteFrequency() {

        String note = sequence[currentNote++];
        System.out.println(note);
        if (currentNote >= sequence.length) {
            currentNote = 0;
        }

        // todo Consider using Float with null values
        if (note.isEmpty()) {
            return 0;
        } else {
            return noteParser.parseNote(note);
        }

    }

}
