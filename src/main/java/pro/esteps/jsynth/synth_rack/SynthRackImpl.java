package pro.esteps.jsynth.synth_rack;

import pro.esteps.jsynth.api.input.DrumMachineMessage;
import pro.esteps.jsynth.api.input.SynthMessage;
import pro.esteps.jsynth.drum_machine.DrumMachine;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.output.Output;
import pro.esteps.jsynth.pubsub.broker.MessageBroker;
import pro.esteps.jsynth.pubsub.message.Message;
import pro.esteps.jsynth.pubsub.subscriber.Subscriber;
import pro.esteps.jsynth.sequencer.EmptyNote;
import pro.esteps.jsynth.sequencer.Note;
import pro.esteps.jsynth.sequencer.Sequencer;
import pro.esteps.jsynth.sequencer.SynthNote;
import pro.esteps.jsynth.synth.Synth;
import pro.esteps.jsynth.wave_generator.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Synth rack containing synthesizers and drum machines.
 */
public class SynthRackImpl implements SynthRack, Subscriber {

    private final MessageBroker messageBroker;
    private static SynthRackImpl synthRack;
    private List<Synth> synths;
    private List<DrumMachine> drumMachines;
    private Mixer mixer;

    private SynthRackImpl(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
        initSynths();
        initDrumMachines();
        initMixer();
        initOutput();
    }

    public static SynthRackImpl getInstance(MessageBroker messageBroker) {
        if (synthRack == null) {
            synthRack = new SynthRackImpl(messageBroker);
        }
        return synthRack;
    }

    /**
     * Init 4 synths.
     * Last synth has 1/4 tempo setting.
     */
    private void initSynths() {
        synths = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                // todo Make synth tempo configurable
                synths.add(i, new Synth(Sequencer.SequencerTempo.QUARTER));
            } else {
                synths.add(i, new Synth());
            }
        }
    }

    /**
     * Init 2 drum machines.
     */
    private void initDrumMachines() {
        drumMachines = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            drumMachines.add(i, new DrumMachine());
        }
    }

    /**
     * Init global mixer.
     */
    private void initMixer() {
        // todo Add volume control
        mixer = new Mixer(6);
        mixer.setProducerForInput(0, synths.get(0), (byte) 80);
        mixer.setProducerForInput(1, synths.get(1), (byte) 80);
        mixer.setProducerForInput(2, synths.get(2), (byte) 80);
        mixer.setProducerForInput(3, synths.get(3), (byte) 80);
        mixer.setProducerForInput(4, drumMachines.get(0), (byte) 80);
        mixer.setProducerForInput(5, drumMachines.get(1), (byte) 50);
    }

    /**
     * Init output  and run its thread.
     */
    private void initOutput() {
        Output output = new Output(mixer, messageBroker);
        Thread outputThread = new Thread(output);
        outputThread.start();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof SynthMessage) {
            handleSynthMessage((SynthMessage) message);
        }
        if (message instanceof DrumMachineMessage) {
            // todo
        }
    }

    private void handleSynthMessage(SynthMessage message) {
        Synth synth = synths.get(message.getSynth());

        // Oscillators
        // todo Change existing generator settings instead of replacing it

        var oscillatorMessages = message.getOscillators();
        for (int i = 0; i < 4 ; i++) {

            Generator generator = null;

            var oscillatorMessage = oscillatorMessages[i];
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SAW) {
                generator = new SawWaveGenerator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SQUARE) {
                generator = new SquareWaveGenerator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SINE) {
                generator = new SineWaveGenerator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.TRIANGLE) {
                generator = new TriangleWaveGenerator();
            }
            // todo Handle other (i.e. missing or invalid) values

            synth.setGenerator(
                    i,
                    generator,
                    oscillatorMessage.getTune(),
                    (byte) oscillatorMessage.getVolume()
            );
        }



        // Sequence

        Note[] notes = new Note[16];
        Note note;
        int i = 0;
        for (var inputNote : message.getSequence()) {
            note = null;
            if (inputNote.isEmpty()) {
                note = new EmptyNote();
            } else if (!inputNote.getNote().isEmpty()) {
                note = new SynthNote(inputNote.getNote());
            }
            notes[i++] = note;
        }

        synth.setSequence(notes);


    }

}
