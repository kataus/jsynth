package pro.esteps.jsynth.sequencer;

import pro.esteps.jsynth.parser.NoteParser;

import static pro.esteps.jsynth.fx.LowPassFilter.LOW_PASS_DEFAULT_FREQUENCY;

public class Sequencer {

    private Note[] sequence = new Note[64];

    private final NoteParser noteParser;

    private Note currentNote;

    private int currentNoteIndex;

    public void setSequence(Note[] sequence) {
        // todo Guard clauses
        this.sequence = sequence;
    }

    public Sequencer() {
        this.noteParser = new NoteParser();
    }

    public void advance() {
        Note note = sequence[currentNoteIndex++];
        if (currentNoteIndex >= sequence.length) {
            currentNoteIndex = 0;
        }
        if (note != null) {
            if (note instanceof EmptyNote) {
                currentNote = null;
            } else {
                currentNote = note;
            }
        }
    }

    public float getCurrentNoteFrequency() {
        if (currentNote == null) {
            return 0;
        }
        return noteParser.parseNote(currentNote.getNote());
    }

    public float getCurrentNoteLowPassFilterFrequency() {
        if (currentNote == null || !(currentNote instanceof SynthNote)) {
            return LOW_PASS_DEFAULT_FREQUENCY;
        }
        return ((SynthNote) currentNote).getLowPassFilterFrequency();
    }

    public byte getCurrentNoteLowPassFilterResonance() {
        if (currentNote == null || !(currentNote instanceof SynthNote)) {
            return 0;
        }
        return ((SynthNote) currentNote).getLowPassFilterResonance();
    }

}
