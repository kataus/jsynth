package pro.esteps.jsynth.synth_rack.contract;

/**
 * Frequency Consumer.
 * <p>
 * Classes that implement this interface usually produce sounds using the provided frequency.
 */
public interface FrequencyConsumer {

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

}
