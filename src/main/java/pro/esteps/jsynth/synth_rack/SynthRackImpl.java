package pro.esteps.jsynth.synth_rack;

import pro.esteps.jsynth.synth_rack.message_handler.DrumMachineMessageHandler;
import pro.esteps.jsynth.synth_rack.message_handler.SynthMessageHandler;
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
 * Synth rack with synthesizers and drum machines.
 */
public class SynthRackImpl implements SynthRack, Subscriber {

    private final MessageBroker messageBroker;
    private static SynthRackImpl synthRack;

    private final SynthMessageHandler synthMessageHandler;
    private final DrumMachineMessageHandler drumMachineMessageHandler;

    private List<Synth> synths;
    private List<DrumMachine> drumMachines;
    private Mixer mixer;

    private SynthRackImpl(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
        this.synthMessageHandler = new SynthMessageHandler(this);
        this.drumMachineMessageHandler = new DrumMachineMessageHandler(this);
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
     * Get a synth by index.
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException if provided index is too large
     */
    @Override
    public Synth getSynthByIndex(int index) {
        if (index > synths.size() - 1) {
            throw new IndexOutOfBoundsException("Synth index is out of bounds: " + index);
        }
        return synths.get(index);
    }

    /**
     * Get a drum machine by index.
     *
     * @param index
     * @return
     * @throws IndexOutOfBoundsException if provided index is too large
     */
    @Override
    public DrumMachine getDrumMachineByIndex(int index) {
        if (index > drumMachines.size() - 1) {
            throw new IndexOutOfBoundsException("DrumMachine index is out of bounds: " + index);
        }
        return drumMachines.get(index);
    }

    /**
     * Handle a PubSub message.
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        if (message instanceof SynthMessage) {
            handleSynthMessage((SynthMessage) message);
        }
        if (message instanceof DrumMachineMessage) {
            handleDrumMachineMessage((DrumMachineMessage) message);
        }
    }

    /**
     * Init 4 synths.
     * 1st synth (bass) has a 1/4 tempo.
     * <p>
     * TODO: Add tempo controls to API
     */
    private void initSynths() {
        synths = new ArrayList<>();
        synths.add(0, new Synth(Sequencer.SequencerTempo.QUARTER));
        for (int i = 1; i < 4; i++) {
            synths.add(i, new Synth());
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
     * Init the global mixer.
     * <p>
     * TODO: Add volume controls to API
     */
    private void initMixer() {
        mixer = new Mixer(6);
        mixer.setProducerForInput(0, synths.get(0), (byte) 80);
        mixer.setProducerForInput(1, synths.get(1), (byte) 80);
        mixer.setProducerForInput(2, synths.get(2), (byte) 80);
        mixer.setProducerForInput(3, synths.get(3), (byte) 80);
        mixer.setProducerForInput(4, drumMachines.get(0), (byte) 80);
        mixer.setProducerForInput(5, drumMachines.get(1), (byte) 50);
    }

    /**
     * Init the output and run its thread.
     */
    private void initOutput() {
        Output output = new Output(mixer, messageBroker);
        Thread outputThread = new Thread(output);
        outputThread.start();
    }

    private void handleSynthMessage(SynthMessage message) {
        synthMessageHandler.handleMessage(message);
    }

    private void handleDrumMachineMessage(DrumMachineMessage message) {
        drumMachineMessageHandler.handleMessage(message);
    }

}
