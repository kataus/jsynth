package pro.esteps.jsynth.synth_rack.drum_machine;

import pro.esteps.jsynth.synth_rack.contract.SoundProducer;
import pro.esteps.jsynth.synth_rack.mixer.EffectsProcessor;
import pro.esteps.jsynth.synth_rack.mixer.Mixer;
import pro.esteps.jsynth.synth_rack.sequencer.DrumMachineNote;
import pro.esteps.jsynth.synth_rack.sequencer.DrumMachineSequencer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pro.esteps.jsynth.synth_rack.config.Config.BUFFER_SIZE;
import static pro.esteps.jsynth.synth_rack.config.Config.TICKS_PER_SEQUENCER_STEP;

/**
 * Drum machine.
 * <p>
 * TODO: Refactor this class
 */
public class DrumMachine implements SoundProducer {

    // TODO: Refactor this class to a separate file
    // TODO: Cache files instead of reloading them
    private static class FileSoundProducer implements SoundProducer {

        private short[] data;

        private int currentCursorIndex;

        public FileSoundProducer(File file) throws IOException, UnsupportedAudioFileException {

            // TODO: Distinguish 44- and 46-byte WAV headers
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {

                int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
                if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                    // some audio formats may have unspecified frame size
                    // in that case we may read any amount of bytes
                    bytesPerFrame = 1;
                }

                long frames = audioInputStream.getFrameLength();
                this.data = new short[(int) frames];

                // Set an arbitrary buffer size of 1024 frames.
                int numBytes = 1024 * bytesPerFrame;
                byte[] audioBytes = new byte[numBytes];

                int numBytesRead = 0;
                int index = 0;

                // Try to read numBytes bytes from the file.
                while ((numBytesRead =
                        audioInputStream.read(audioBytes)) != -1) {
                    if (numBytesRead != 0) {
                        for (int i = 0; i < numBytesRead / 2; i++) {
                            data[index++] = ((short) ((audioBytes[i * 2] & 0xff) | (audioBytes[i * 2 + 1] << 8)));
                        }
                    }
                }

            } catch (Exception e) {
                // TODO: Handle exceptions
                throw e;
            }

        }

        /**
         * Check whether the file has not been completely processed yet.
         *
         * @return
         */
        public boolean hasRemainingData() {
            return currentCursorIndex < data.length;
        }

        @Override
        public short[] getSoundChunk() {
            short[] chunk = new short[BUFFER_SIZE];
            for (int i = 0; i < BUFFER_SIZE; i++) {
                if (!hasRemainingData()) {
                    break;
                }
                chunk[i] = data[currentCursorIndex++];
            }
            return chunk;
        }
    }

    // TODO: Refactor this class to a separate file
    private static class DrumMachineSoundProducer implements SoundProducer {

        private short[] mix = new short[0];

        private String[] notes = new String[0];

        private final List<FileSoundProducer> fileSoundProducers = new ArrayList<>();

        private void regenerateMix() {
            // TODO: Implement this method
        }

        /**
         * Update file sound producers.
         *
         * @param notes
         * @throws IOException
         * @throws UnsupportedAudioFileException
         */
        public void setNotes(String[] notes) throws IOException, UnsupportedAudioFileException {

            this.notes = notes;

            List<String> nonEmptyNotes =
                    Arrays.stream(notes)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());

            if (!nonEmptyNotes.isEmpty()) {
                // regenerateMix();
                for (String note : nonEmptyNotes) {
                    // TODO: Load file as a resource
                    // TODO: Handle file errors
                    File file = new File("src/main/resources/drums/" + note + ".wav");
                    FileSoundProducer fileSoundProducer = new FileSoundProducer(file);
                    fileSoundProducers.add(fileSoundProducer);
                }
            }

        }

        @Override
        public short[] getSoundChunk() {

            short[] chunk = new short[BUFFER_SIZE];

            if (fileSoundProducers.isEmpty()) {
                return chunk;
            }

            short[] producerChunk;
            int sample;
            List<FileSoundProducer> producersToRemove = new ArrayList<>();

            for (FileSoundProducer fileSoundProducer : fileSoundProducers) {
                producerChunk = fileSoundProducer.getSoundChunk();
                for (int i = 0; i < producerChunk.length; i++) {
                    sample = chunk[i] + producerChunk[i];
                    // TODO: Use clipping algorithm
                    if (sample > Short.MAX_VALUE) {
                        sample = Short.MAX_VALUE;
                    } else if (sample < Short.MIN_VALUE) {
                        sample = Short.MIN_VALUE;
                    }
                    chunk[i] = (short) sample;
                }
                if (!fileSoundProducer.hasRemainingData()) {
                    producersToRemove.add(fileSoundProducer);
                }
            }

            if (!producersToRemove.isEmpty()) {
                for (FileSoundProducer fileSoundProducer : producersToRemove) {
                    fileSoundProducers.remove(fileSoundProducer);
                }
            }

            return chunk;
        }
    }

    private DrumMachineSequencer sequencer;

    private final DrumMachineSoundProducer drumMachineSoundProducer;

    private final Mixer generatorMixer;

    private final EffectsProcessor effectsProcessor;

    private final Mixer outputMixer;

    private int currentChunk = 1;

    public DrumMachine() {

        this.sequencer = new DrumMachineSequencer();
        this.sequencer.setSequence(new DrumMachineNote[]{
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
        });

        this.drumMachineSoundProducer = new DrumMachineSoundProducer();

        this.generatorMixer = new Mixer(1);

        generatorMixer.setProducerForInput(0, drumMachineSoundProducer, (byte) 80);

        this.effectsProcessor = new EffectsProcessor(generatorMixer);

        this.outputMixer = new Mixer(1);
        outputMixer.setProducerForInput(0, effectsProcessor, (byte) 100);
    }

    public void setSequencer(DrumMachineSequencer sequencer) {
        this.sequencer = sequencer;
    }

    @Override
    public short[] getSoundChunk() {

        if (currentChunk == 1) {
            sequencer.advance();
            String[] samples = sequencer.getCurrentNoteSamples();
            try {
                drumMachineSoundProducer.setNotes(samples);
            } catch (IOException | UnsupportedAudioFileException e) {
                // TODO: Handle exceptions
            }
        }
        currentChunk++;
        if (currentChunk > TICKS_PER_SEQUENCER_STEP) {
            currentChunk = 1;
        }

        short[] chunk = new short[BUFFER_SIZE];
        // TODO: Use FX Mixer
        System.arraycopy(outputMixer.getSoundChunk(), 0, chunk, 0, BUFFER_SIZE);
        return chunk;

    }

    /**
     * Set new sequence.
     *
     * @param notes
     */
    public void setSequence(DrumMachineNote[] notes) {
        this.sequencer.setSequence(notes);
    }

}
