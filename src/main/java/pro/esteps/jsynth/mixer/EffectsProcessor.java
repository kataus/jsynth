package pro.esteps.jsynth.mixer;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.fx.Bypass;
import pro.esteps.jsynth.fx.Effect;
import pro.esteps.jsynth.fx.FixedDelay;
import pro.esteps.jsynth.fx.LowPassFilter;

import java.util.ArrayList;
import java.util.List;

public class EffectsProcessor implements SoundConsumer, SoundProducer {

    private SoundProducer producer;

    // todo Max 2 effects
    private final List<Effect> effects = new ArrayList<>();

    public EffectsProcessor(SoundProducer producer) {
        this.producer = producer;
        this.effects.add(0, new Bypass(this.producer));
        this.effects.add(1, new Bypass((SoundProducer) this.effects.get(0)));
    }

    @Override
    public short[] getSoundChunk() {
        SoundProducer lastEffect = (SoundProducer) this.effects.get(1);
        return lastEffect.getSoundChunk();
    }

    public void setCutoffFrequency(float frequency) {
        Effect effect = effects.get(0);
        if (!(effect instanceof LowPassFilter)) {
            this.effects.set(0, new LowPassFilter(producer, frequency, (byte) 0));
            this.effects.set(1, new Bypass((SoundProducer) this.effects.get(0)));
        } else {
            ((LowPassFilter) effect).setFrequency(frequency);
        }
    }

    public void setResonance(byte resonance) {
        Effect effect = effects.get(0);
        if (!(effect instanceof LowPassFilter)) {
            // todo Handle error
        } else {
            ((LowPassFilter) effect).setResonanceAmount(resonance);
        }
    }

    public void enableDelay() {
        this.effects.set(1, new FixedDelay((SoundProducer) this.effects.get(0)));
    }

}
