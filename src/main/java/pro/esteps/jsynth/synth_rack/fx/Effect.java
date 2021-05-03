package pro.esteps.jsynth.synth_rack.fx;

import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

/**
 * Effect.
 * <p>
 * Classes that implement this interface usually alter the provided sound.
 */
public interface Effect {

    void setProducer(SoundProducer producer);

}
