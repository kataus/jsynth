package pro.esteps.jsynth.sequencer;

public class DrumMachineNote {

    private final String[] samples;

    public DrumMachineNote(String[] samples) {
        this.samples = samples;
    }

    public String[] getSamples() {
        // todo Use immutable value
        String[] samplesCopy = new String[samples.length];
        System.arraycopy(samples, 0, samplesCopy, 0, samples.length);
        return samplesCopy;
    }

}
