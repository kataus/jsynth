package pro.esteps.jsynth.mixer;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.fx.Bypass;
import pro.esteps.jsynth.fx.Effect;

import java.util.ArrayList;
import java.util.List;

public class EffectsProcessor implements SoundConsumer, SoundProducer {

    // todo Max 2 effects
    private final List<Effect> effects = new ArrayList<>();

    public EffectsProcessor(SoundProducer producer) {
        this.effects.add(0, new Bypass(producer));
        this.effects.add(1, new Bypass((SoundProducer) this.effects.get(0)));
    }

    @Override
    public short[] getSoundChunk() {
        SoundProducer lastEffect = (SoundProducer) this.effects.get(1);
        return lastEffect.getSoundChunk();
    }
}
