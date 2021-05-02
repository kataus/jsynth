package pro.esteps.jsynth.synth_rack.fx;

import pro.esteps.jsynth.synth_rack.contract.SoundConsumer;
import pro.esteps.jsynth.synth_rack.contract.SoundProducer;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

public class LowHighPassFilter implements SoundConsumer, SoundProducer {

    // rez amount, from sqrt(2) to ~ 0.1
    private float resonance;

    private float frequency;

    private int sampleRate;

    private PassType passType;

    public float value;

    private float c, a1, a2, a3, b1, b2;

    private SoundProducer producer;

    // Array of input values, latest are in front
    private final float[] inputHistory = new float[2];

    // Array of output values, latest are in front
    private final float[] outputHistory = new float[3];

    public LowHighPassFilter(SoundProducer producer, float frequency, PassType passType, float resonance) {
        this.producer = producer;
        this.resonance = resonance;
        this.frequency = frequency;
        this.sampleRate = SAMPLE_RATE;
        this.passType = passType;
        calculateValues();
    }

    private void calculateValues() {
        switch (passType) {
            case Lowpass -> {
                c = 1.0f / (float) Math.tan(Math.PI * frequency / sampleRate);
                a1 = 1.0f / (1.0f + resonance * c + c * c);
                a2 = 2f * a1;
                a3 = a1;
                b1 = 2.0f * (1.0f - c * c) * a1;
                b2 = (1.0f - resonance * c + c * c) * a1;
            }
            case Highpass -> {
                c = (float) Math.tan(Math.PI * frequency / sampleRate);
                a1 = 1.0f / (1.0f + resonance * c + c * c);
                a2 = -2f * a1;
                a3 = a1;
                b1 = 2.0f * (c * c - 1.0f) * a1;
                b2 = (1.0f - resonance * c + c * c) * a1;
            }
        }
    }

    public enum PassType {
        Highpass,
        Lowpass,
    }

    private void update(float newInput) {
        float newOutput = a1 * newInput + a2 * this.inputHistory[0] + a3 * this.inputHistory[1] - b1 * this.outputHistory[0] - b2 * this.outputHistory[1];

        this.inputHistory[1] = this.inputHistory[0];
        this.inputHistory[0] = newInput;

        this.outputHistory[2] = this.outputHistory[1];
        this.outputHistory[1] = this.outputHistory[0];
        this.outputHistory[0] = newOutput;
    }

    private float getValue() {
        return this.outputHistory[0];
    }

    @Override
    public short[] getSoundChunk() {

        short[] mixedChunk = new short[BUFFER_SIZE];
        short[] producerChunk = producer.getSoundChunk();

        for (int i = 0; i < BUFFER_SIZE; i++) {
            update(producerChunk[i]);
            mixedChunk[i] = (short) getValue();
        }

        return mixedChunk;
    }

}
