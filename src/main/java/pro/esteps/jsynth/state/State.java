package pro.esteps.jsynth.state;

public class State {

    private volatile Float frequency;

    public Float getFrequency() {
        return frequency;
    }

    public void setFrequency(Float frequency) {
        assert frequency != null;
        this.frequency = frequency;
    }

    public void clearFrequency() {
        this.frequency = null;
    }

}
