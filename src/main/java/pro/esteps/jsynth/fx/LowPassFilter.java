package pro.esteps.jsynth.fx;

import pro.esteps.jsynth.contract.SoundConsumer;
import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.iirj.Butterworth;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

public class LowPassFilter implements SoundConsumer, SoundProducer {

    private static final int BAND_PASS_WIDTH_FREQUENCY = 800;

    private SoundProducer producer;

    private float frequency;

    // Positive 0..100
    private byte resonanceAmount;

    private Butterworth butterworthLowPassFilter;

    private Butterworth butterworthBandPassFilter;

    public LowPassFilter(SoundProducer producer, float frequency, byte resonanceAmount) {

        assert resonanceAmount >= 0 && resonanceAmount <= 100;

        this.producer = producer;
        this.frequency = frequency;
        this.resonanceAmount = resonanceAmount;

        this.butterworthLowPassFilter = new Butterworth();
        butterworthLowPassFilter.lowPass(8, SAMPLE_RATE, this.frequency);

        this.butterworthBandPassFilter = new Butterworth();
        butterworthBandPassFilter.bandPass(8, SAMPLE_RATE, this.frequency, BAND_PASS_WIDTH_FREQUENCY);

    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
        butterworthLowPassFilter.lowPass(8, SAMPLE_RATE, frequency);
    }

    public void setResonanceAmount(byte resonanceAmount) {
        this.resonanceAmount = resonanceAmount;
        butterworthBandPassFilter.bandPass(8, SAMPLE_RATE, frequency, BAND_PASS_WIDTH_FREQUENCY);
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        /*
        frequency += 30;
        butterworthLowPassFilter.lowPass(8, SAMPLE_RATE, frequency);
        butterworthBandPassFilter.bandPass(8, SAMPLE_RATE, frequency, BAND_PASS_WIDTH_FREQUENCY);
        */

        double sample;

        for (int i = 0; i < BUFFER_SIZE; i++) {

            // Low Pass
            sample = (short) butterworthLowPassFilter.filter(producerChunk[i]);

            // Band Pass
            // todo Use log. volume control
            if (resonanceAmount > 0) {
                sample += (short) (butterworthBandPassFilter.filter(producerChunk[i]) / 100 * resonanceAmount);
            }

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
