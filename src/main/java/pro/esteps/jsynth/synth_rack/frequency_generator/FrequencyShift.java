package pro.esteps.jsynth.synth_rack.frequency_generator;

import pro.esteps.jsynth.synth_rack.contract.FrequencyConsumer;
import pro.esteps.jsynth.synth_rack.contract.FrequencyProducer;

/**
 * Frequency shift generator.
 * <p>
 * This generator alters the provided frequency using the delta (tune) value.
 */
public class FrequencyShift implements FrequencyConsumer, FrequencyProducer {

    private FrequencyConsumer consumer;

    private float delta;

    public FrequencyShift(float delta) {
        this.delta = delta;
    }

    @Override
    public void setFrequency(float frequency) {
        consumer.setFrequency(frequency + (frequency / 100 * delta));
    }

    @Override
    public void clearFrequency() {
        consumer.clearFrequency();
    }

    @Override
    public void addConsumer(FrequencyConsumer consumer) {
        // TODO: Consider using Set<FrequencyConsumer>
        this.consumer = consumer;
    }
}
