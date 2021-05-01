package pro.esteps.jsynth.api.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import pro.esteps.jsynth.pubsub.message.Message;

/**
 * Сообщение, которое приходит при каждом изменении в веб-секвенсоре.
 */
public class SynthMessage extends Message {

    public SynthMessage() {
    }

    protected SynthMessage(String type) {
        super(type);
    }

    public enum Waveform {

        SAW("saw"),
        SQUARE("square"),
        SINE("sine"),
        TRIANGLE("triangle");

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

    public static class Note {

        private boolean isEmpty;
        private String note;

        public boolean isEmpty() {
            return isEmpty;
        }

        public String getNote() {
            return note;
        }
    }

    private int synth;
    private Oscillator[] oscillators;
    @JsonProperty("has_decay")
    private boolean hasDecay;
    @JsonProperty("has_delay")
    private boolean hasDelay;
    private int cutoff;
    private int resonance;
    private Note[] sequence;

    public String getType() {
        return type;
    }

    public int getSynth() {
        return synth;
    }

    public Oscillator[] getOscillators() {
        return oscillators;
    }

    public boolean hasDecay() {
        return hasDecay;
    }

    public boolean hasDelay() {
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
