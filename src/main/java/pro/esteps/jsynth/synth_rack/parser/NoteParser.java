package pro.esteps.jsynth.synth_rack.parser;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NoteParser {

    private static final Map<String, Float> frequencies = new HashMap<>();

    static {
        // todo Store full list with octaves
        frequencies.put("C", 261.63f);
        frequencies.put("C#", 277.18f);
        frequencies.put("D", 293.66f);
        frequencies.put("D#", 311.13f);
        frequencies.put("E", 329.63f);
        frequencies.put("F", 349.23f);
        frequencies.put("F#", 369.99f);
        frequencies.put("G", 392.00f);
        frequencies.put("G#", 415.30f);
        frequencies.put("A", 440.00f);
        frequencies.put("A#", 466.16f);
        frequencies.put("B", 493.88f);
    }

    // todo Validate note
    // todo Use regex
    public float parseNote(String note) {

        if (note == null) {
            return 0;
        }

        String letter;
        int octave;

        if (note.length() == 2) {
            letter = note.substring(0, 1);
            octave = Integer.parseInt(note.substring(1, 2));
        } else {
            letter = note.substring(0, 2);
            octave = Integer.parseInt(note.substring(2, 3));
        }

        letter = letter.toUpperCase(Locale.ROOT);
        float frequency = frequencies.get(letter);

        assert octave >= 1 && octave <= 6;
        int delta = Math.abs(4 - octave);
        if (octave < 4) {
            frequency /= Math.pow(2, delta);
        }
        if (octave > 4) {
            frequency *= Math.pow(2, delta);
        }

        return frequency;
    }

}
