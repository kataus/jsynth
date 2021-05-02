package pro.esteps.jsynth.synth_rack;

import pro.esteps.jsynth.synth_rack.drum_machine.DrumMachine;
import pro.esteps.jsynth.synth_rack.synth.Synth;

/**
 * Synth rack with synthesizers and drum machines.
 */
public interface SynthRack {

    /**
     * Get a synth by index.
     *
     * @param index
     * @return
     */
    Synth getSynthByIndex(int index);

    /**
     * Get a drum machine by index.
     *
     * @param index
     * @return
     */
    DrumMachine getDrumMachineByIndex(int index);

}
