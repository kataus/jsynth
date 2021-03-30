package pro.esteps.jsynth.frequency_generator;

import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.contract.FrequencyProducer;

import java.util.LinkedHashSet;
import java.util.Set;

public class FixedFrequencyGenerator implements FrequencyProducer {

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
