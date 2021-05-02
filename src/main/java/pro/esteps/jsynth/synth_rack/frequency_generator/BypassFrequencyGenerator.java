package pro.esteps.jsynth.synth_rack.frequency_generator;

import pro.esteps.jsynth.synth_rack.contract.FrequencyConsumer;
import pro.esteps.jsynth.synth_rack.contract.FrequencyProducer;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Bypass frequency generator.
 * <p>
 * This generator passes the provided frequency to its consumers as is.
 */
public class BypassFrequencyGenerator implements FrequencyProducer {

    private final Set<FrequencyConsumer> consumers = new LinkedHashSet<>();

    @Override
    public void setFrequency(float frequency) {
        this.consumers.forEach(c -> c.setFrequency(frequency));
    }

    @Override
    public void clearFrequency() {
        this.consumers.forEach(FrequencyConsumer::clearFrequency);
    }

    @Override
    public void addConsumer(FrequencyConsumer consumer) {
        this.consumers.add(consumer);
    }

}
