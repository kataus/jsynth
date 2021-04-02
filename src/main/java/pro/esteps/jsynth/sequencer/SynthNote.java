package pro.esteps.jsynth.sequencer;

import static pro.esteps.jsynth.fx.LowPassFilter.LOW_PASS_DEFAULT_FREQUENCY;

public class SynthNote extends Note {

    private final String note;

    private float lowPassFilterFrequency = LOW_PASS_DEFAULT_FREQUENCY;

    private byte lowPassFilterResonance = 0;

    public SynthNote(String note) {
        // todo Guard clauses
        this.note = note;
    }

    public SynthNote(String note, float lowPassFilterFrequency, byte lowPassFilterResonance) {
        // todo Guard clauses
        this.note = note;
        this.lowPassFilterFrequency = lowPassFilterFrequency;
        this.lowPassFilterResonance = lowPassFilterResonance;
    }

    @Override
    public String getNote() {
        return note;
    }

    public float getLowPassFilterFrequency() {
        return lowPassFilterFrequency;
    }

    public byte getLowPassFilterResonance() {
        return lowPassFilterResonance;
    }
}
