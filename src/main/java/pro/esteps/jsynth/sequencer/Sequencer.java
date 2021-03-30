package pro.esteps.jsynth.sequencer;

import pro.esteps.jsynth.contract.FrequencyConsumer;
import pro.esteps.jsynth.parser.NoteParser;

public class Sequencer implements Runnable {

    private FrequencyConsumer frequencyConsumer;

    private String[] sequence = new String[16];

    private NoteParser noteParser;

    public void setFrequencyConsumer(FrequencyConsumer frequencyConsumer) {
        this.frequencyConsumer = frequencyConsumer;
    }

    public void setSequence(String[] sequence) {
        this.sequence = sequence;
    }

    public Sequencer() {
        this.noteParser = new NoteParser();
    }

    @Override
    public void run() {
        try {

            do {
                for (String note : sequence) {
                    if (note.isEmpty()) {
                        frequencyConsumer.clearFrequency();
                    } else {
                        float frequency = noteParser.parseNote(note);
                        frequencyConsumer.setFrequency(frequency);
                    }
                    Thread.sleep(200);
                }
            } while (true);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
