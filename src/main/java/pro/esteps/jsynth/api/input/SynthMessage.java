package pro.esteps.jsynth.api.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import pro.esteps.jsynth.pubsub.message.Message;

/**
 * Сообщение, которое приходит при каждом изменении в веб-секвенсоре.
 */
public class SynthMessage implements Message {

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

    public enum Key {

        A("a"),
        B("b"),
        C("c"),
        D("d"),
        E("e"),
        F("f"),
        G("g"),
        H("h");

        private final String value;

        Key(String value) {
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
        public static Key fromValue(String text) {
            for (Key b : Key.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public static class Note {

        private boolean isEmpty;
        private Key key;
        private boolean isSharp;
        private int octave;

        public boolean isEmpty() {
            return isEmpty;
        }

        public Key getKey() {
            return key;
        }

        public boolean isSharp() {
            return isSharp;
        }

        public int getOctave() {
            return octave;
        }

    }

    // todo Use enum
    private String type;
    private String synth;
    private Oscillator oscillator;
    private boolean hasDecay;
    private boolean hasDelay;
    private int cutoff;
    private int resonance;
    private Note[] sequence;

    public String getType() {
        return type;
    }

    public String getSynth() {
        return synth;
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    public boolean isHasDecay() {
        return hasDecay;
    }

    public boolean isHasDelay() {
        return hasDelay;
    }

    public int getCutoff() {
        return cutoff;
    }

    public int getResonance() {
        return resonance;
    }

    public Note[] getSequence() {
        return sequence;
    }

}
