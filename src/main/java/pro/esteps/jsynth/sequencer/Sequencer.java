package pro.esteps.jsynth.sequencer;

import pro.esteps.jsynth.parser.NoteParser;

import static pro.esteps.jsynth.fx.LowPassFilter.LOW_PASS_DEFAULT_FREQUENCY;

public class Sequencer {

    private Note[] sequence = new Note[16];

    private final NoteParser noteParser;

    private Note currentNote;

    private byte currentNoteIndex;

    public enum SequencerTempo {

        ONE((byte) 1),
        HALF((byte) 2),
        QUARTER((byte) 4);

        public final byte multiplier;

        SequencerTempo(byte multiplier) {
            this.multiplier = multiplier;
        }

    }

    private SequencerTempo tempo;

    private byte currentTempoIndex;

    public void setSequence(Note[] sequence) {
        // todo Guard clauses
        this.sequence = sequence;
    }

    public Sequencer() {
        this.noteParser = new NoteParser();
        this.tempo = SequencerTempo.ONE;
    }

    public Sequencer(SequencerTempo tempo) {
        this.noteParser = new NoteParser();
        this.tempo = tempo;
    }

    public void advance() {

        if (currentTempoIndex++ == 0) {
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

        if (currentTempoIndex >= tempo.multiplier) {
            currentTempoIndex = 0;
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

    public byte getCurrentDecayLength() {
        if (currentNote == null || !(currentNote instanceof SynthNote)) {
            return 0;
        }
        return ((SynthNote) currentNote).getDecayLength();
    }

    public void setTempo(SequencerTempo tempo) {
        if (this.tempo != tempo) {
            this.tempo = tempo;
        }
    }

}
