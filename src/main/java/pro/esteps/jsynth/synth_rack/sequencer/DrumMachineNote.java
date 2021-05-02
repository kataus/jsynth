package pro.esteps.jsynth.synth_rack.sequencer;

/**
 * Drum machine note.
 */
public class DrumMachineNote {

    private final String[] samples;

    public DrumMachineNote(String[] samples) {
        this.samples = samples;
    }

    public String[] getSamples() {
        // TODO: Use immutable value
        String[] samplesCopy = new String[samples.length];
        System.arraycopy(samples, 0, samplesCopy, 0, samples.length);
        return samplesCopy;
    }

}
