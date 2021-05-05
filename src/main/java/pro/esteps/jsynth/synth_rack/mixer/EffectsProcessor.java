package pro.esteps.jsynth.synth_rack.mixer;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.synth_rack.fx.Bypass;
import pro.esteps.jsynth.synth_rack.fx.Effect;
import pro.esteps.jsynth.synth_rack.fx.FixedDelay;
import pro.esteps.jsynth.synth_rack.fx.LowPassFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Effects processor mixer.
 */
public class EffectsProcessor implements SoundConsumer, SoundProducer {

    private final SoundProducer producer;

    // TODO: Max 2 effects
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

        Effect firstEffect = effects.get(0);
        Effect secondEffect = effects.get(1);

        if (!(firstEffect instanceof LowPassFilter)) {
            effects.set(0, new LowPassFilter(producer, frequency, (byte) 0));
        } else {
            ((LowPassFilter) firstEffect).setFrequency(frequency);
        }

        if (secondEffect == null) {
            effects.set(1, new Bypass((SoundProducer) effects.get(0)));
        } else {
            secondEffect.setProducer((SoundProducer) effects.get(0));
        }
    }

    public void setResonance(byte resonance) {
        Effect effect = effects.get(0);
        if (!(effect instanceof LowPassFilter)) {
            // TODO: Handle error
        } else {
            ((LowPassFilter) effect).setResonanceAmount(resonance);
        }
    }

    public void enableDelay() {
        Effect secondEffect = effects.get(1);
        if (!(secondEffect instanceof FixedDelay)) {
            effects.set(1, new FixedDelay((SoundProducer) effects.get(0)));
        }
    }

    public void disableDelay() {
        this.effects.set(1, new Bypass((SoundProducer) this.effects.get(0)));
    }

}
