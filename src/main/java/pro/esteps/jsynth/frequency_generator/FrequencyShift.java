package pro.esteps.jsynth.frequency_generator;

import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.contract.FrequencyProducer;

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
        // todo Consider using Set<FrequencyConsumer>
        this.consumer = consumer;
    }
}
