package pro.esteps.jsynth.synth_rack.contract;

/**
 * Frequency Producer.
 * <p>
 * Classes that implement this interface usually process the initial frequency and then pass the new value
 * to their Frequency Consumer(s).
 */
public interface FrequencyProducer {

    /**
     * Set a new frequency.
     *
     * @param frequency
     */
    public void setFrequency(float frequency);

    /**
     * Clear current frequency.
     */
    public void clearFrequency();

    /**
     * Add a Frequency Consumer.
     */
    public void addConsumer(FrequencyConsumer consumer);

}
