package pro.esteps.jsynth.synth_rack.contract;

/**
 * Sound Producer.
 * <p>
 * Classes that implement this interface usually produce sounds.
 */
public interface SoundProducer {
    public short[] getSoundChunk();
}
