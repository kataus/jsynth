package pro.esteps.jsynth.api.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import pro.esteps.jsynth.pubsub.message.Message;

/**
 * Сообщение, которое приходит при каждом изменении в веб-секвенсоре.
 */
public class SequencerMessage implements Message {

    public enum Waveform {

        SAW("saw"),
        SQUARE("square"),
        SINE("sine"),
        TRIANGLE("sine");

        private final String value;

        Waveform(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static Waveform fromValue(String text) {
            for (Waveform b : Waveform.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }


    }

    public static class Oscillator {

        private int index;
        private Waveform waveform;
        private int volume;
        private int tune;

        public int getIndex() {
            return index;
        }

        public Waveform getWaveform() {
            return waveform;
        }

        public int getVolume() {
            return volume;
        }

        public int getTune() {
            return tune;
        }
    }

    private String synth;
    private Oscillator oscillator;

    public String getSynth() {
        return synth;
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

}
