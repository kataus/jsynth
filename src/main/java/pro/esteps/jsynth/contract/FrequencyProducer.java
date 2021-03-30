package pro.esteps.jsynth.contract;

public interface FrequencyProducer {

    public void setFrequency(float frequency);

    public void clearFrequency();

    public void addConsumer(FrequencyConsumer consumer);

}
