package pro.esteps.jsynth.synth_rack;

import pro.esteps.jsynth.websocket_api.input.DrumMachineMessage;
import pro.esteps.jsynth.websocket_api.input.SynthMessage;
import pro.esteps.jsynth.synth_rack.drum_machine.DrumMachine;
import pro.esteps.jsynth.synth_rack.mixer.Mixer;
import pro.esteps.jsynth.synth_rack.output.Output;
import pro.esteps.jsynth.messaging.broker.MessageBroker;
import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.messaging.subscriber.Subscriber;
import pro.esteps.jsynth.synth_rack.sequencer.*;
import pro.esteps.jsynth.synth_rack.synth.Synth;
import pro.esteps.jsynth.synth_rack.oscillator.*;

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
            handleDrumMachineMessage((DrumMachineMessage) message);
        }
    }

    private void handleSynthMessage(SynthMessage message) {
        Synth synth = synths.get(message.getIndex());

        // Oscillators
        // todo Change existing generator settings instead of replacing it

        var oscillatorMessages = message.getOscillators();
        for (int i = 0; i < 4; i++) {

            Oscillator oscillator = null;

            var oscillatorMessage = oscillatorMessages[i];
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SAW) {
                oscillator = new SawWaveOscillator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SQUARE) {
                oscillator = new SquareWaveOscillator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.SINE) {
                oscillator = new SineWaveOscillator();
            }
            if (oscillatorMessage.getWaveform() == SynthMessage.Waveform.TRIANGLE) {
                oscillator = new TriangleWaveOscillator();
            }
            // todo Handle other (i.e. missing or invalid) values

            synth.setGenerator(
                    i,
                    oscillator,
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

        // Effects
        // todo Change existing values instead of replacing them

        synth.setCutoffFrequency(message.getCutoff());

        synth.setResonance((byte) message.getResonance());

        if (message.hasDecay()) {
            synth.setDecayLength((byte) 1);
        } else {
            synth.setDecayLength((byte) 0);
        }

        // todo Disable delay
        if (message.hasDelay()) {
            synth.enableDelay();
        }

    }

    private void handleDrumMachineMessage(DrumMachineMessage message){
        DrumMachine drumMachine = drumMachines.get(message.getIndex());

        // Sequence
        DrumMachineNote[] notes = new DrumMachineNote[16];
        DrumMachineNote note;
        int i = 0;
        for (var inputNote : message.getSequence()) {
            note = new DrumMachineNote(inputNote.getSamples());
            notes[i++] = note;
        }

        drumMachine.setSequence(notes);
    }

}