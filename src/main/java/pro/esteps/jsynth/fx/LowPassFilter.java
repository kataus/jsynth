package pro.esteps.jsynth.fx;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import uk.me.berndporr.iirj.Butterworth;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

public class LowPassFilter implements SoundConsumer, SoundProducer {

    private SoundProducer producer;

    private float frequency;

    private Butterworth butterworth;

    public LowPassFilter(SoundProducer producer, float frequency) {
        this.producer = producer;
        this.frequency = frequency;
        this.butterworth = new Butterworth();
        butterworth.lowPass(8, SAMPLE_RATE, this.frequency);
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        double sample;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            sample = (short) butterworth.filter(producerChunk[i]);
            if (sample > Short.MAX_VALUE) {
                sample = Short.MAX_VALUE;
            } else if (sample < Short.MIN_VALUE) {
                sample = Short.MIN_VALUE;
            }
            mixedChunk[i] = (short) sample;
        }

        return mixedChunk;
    }

}
