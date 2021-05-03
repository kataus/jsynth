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
            // TODO: Handle error
        } else {
            ((LowPassFilter) effect).setResonanceAmount(resonance);
        }
    }

    public void enableDelay() {
        this.effects.set(1, new FixedDelay((SoundProducer) this.effects.get(0)));
    }

    public void disableDelay() {
        this.effects.set(1, new Bypass((SoundProducer) this.effects.get(0)));
    }

}
