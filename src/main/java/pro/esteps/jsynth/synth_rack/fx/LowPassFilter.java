package pro.esteps.jsynth.synth_rack.fx;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.lib.iirj.Butterworth;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

public class LowPassFilter implements Effect, SoundConsumer, SoundProducer {

    public static final float LOW_PASS_DEFAULT_FREQUENCY = 20000f;

    private static final int BAND_PASS_DEFAULT_WIDTH_FREQUENCY = 600;

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
        butterworthBandPassFilter.bandPass(8, SAMPLE_RATE, this.frequency, calculateBandPassWidthFrequency());

    }

    public void setFrequency(float frequency) {
        if (this.frequency != frequency) {
            this.frequency = frequency;
            butterworthLowPassFilter.lowPass(8, SAMPLE_RATE, frequency);
            butterworthBandPassFilter.bandPass(8, SAMPLE_RATE, frequency, calculateBandPassWidthFrequency());
        }
    }

    public void setResonanceAmount(byte resonanceAmount) {
        if (this.resonanceAmount != resonanceAmount) {
            this.resonanceAmount = resonanceAmount;
            butterworthBandPassFilter.bandPass(8, SAMPLE_RATE, frequency, calculateBandPassWidthFrequency());
        }
    }

    private int calculateBandPassWidthFrequency() {
        int result = (int) (frequency / 6);
        return Math.max(result, BAND_PASS_DEFAULT_WIDTH_FREQUENCY);
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        double sample;

        for (int i = 0; i < BUFFER_SIZE; i++) {

            sample = 0;

            // Low Pass
            sample += (short) butterworthLowPassFilter.filter(producerChunk[i]);

            // Band Pass
            // todo Use log. volume control
            if (resonanceAmount > 0) {
                sample += (short) (butterworthBandPassFilter.filter(sample) / 100 * resonanceAmount * 4);
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
