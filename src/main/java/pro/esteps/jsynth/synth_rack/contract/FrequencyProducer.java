package pro.esteps.jsynth.synth_rack.contract;

public interface FrequencyProducer {

    public void setFrequency(float frequency);

    public void clearFrequency();

    public void addConsumer(FrequencyConsumer consumer);

}
