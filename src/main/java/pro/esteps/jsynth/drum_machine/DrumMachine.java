package pro.esteps.jsynth.drum_machine;

import pro.esteps.jsynth.contract.SoundProducer;
import pro.esteps.jsynth.fx.FixedDelay;
import pro.esteps.jsynth.fx.LowPassFilter;
import pro.esteps.jsynth.mixer.Mixer;
import pro.esteps.jsynth.sequencer.DrumMachineSequencer;
import pro.esteps.jsynth.sequencer.Sequencer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pro.esteps.jsynth.App.BUFFER_SIZE;
import static pro.esteps.jsynth.App.SAMPLE_RATE;

public class DrumMachine implements SoundProducer {

    // todo Cache files instead of loading them again
    private static class FileSoundProducer implements SoundProducer {

        private short[] data;

        private int currentCursorIndex;

        public FileSoundProducer(File file) throws IOException, UnsupportedAudioFileException {

            // todo Distinguish 44- and 46-byte WAV headers

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);

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

            int k = 1;

        }

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

    private static class DrumMachineSoundProducer implements SoundProducer {


        /**
         * Cursor position within current period.
         */
        private int currentMixCursorIndex;

        private short[] mix = new short[0];

        private String[] notes = new String[0];

        private List<FileSoundProducer> fileSoundProducers = new ArrayList<>();

        private void regenerateMix() {

            /*
            // todo

            int currentMixSize = mix.length;
            int newMixSize = 0; // todo
            mix = new short[newMixSize];

            if (currentMixSize == 0) {
                currentMixCursorIndex = 0;
            } else if (currentMixCursorIndex > 0) {
                if (currentMixCursorIndex == currentMixSize - 1) {
                    currentMixCursorIndex = newMixSize - 1;
                } else {
                    currentMixCursorIndex = currentMixCursorIndex * newMixSize / currentMixSize;
                }
            }
            */

        }

        public void setNotes(String notes[]) throws IOException, UnsupportedAudioFileException {
            this.notes = notes;
            List<String> nonEmptyNotes =
                    Arrays.stream(notes)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
            if (!nonEmptyNotes.isEmpty()) {
                // regenerateMix();
                for (String note : nonEmptyNotes) {
                    // todo Load file as a resource
                    // todo handle file errors
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
                    // todo Use clipping algorithm
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

    /**
     * Cursor position within current mix.
     */
    private int currentMixCursorIndex;

    private DrumMachineSoundProducer drumMachineSoundProducer;

    private Mixer generatorMixer;

    private Mixer outputMixer;

    private FixedDelay fixedDelay;

    private LowPassFilter lowPassFilter;

    private DrumMachineSequencer sequencer;

    // todo Duplicate code
    private static final int CHUNKS_PER_NOTE = 5;

    // todo Duplicate code
    private int currentChunk = 1;

    public DrumMachine() {
        this.drumMachineSoundProducer = new DrumMachineSoundProducer();
        this.generatorMixer = new Mixer(1);
        generatorMixer.setProducerForInput(0, drumMachineSoundProducer, (byte) 50);
        // this.fixedDelay = new FixedDelay(generatorMixer);
        this.outputMixer = new Mixer(1);
        outputMixer.setProducerForInput(0, (SoundProducer) generatorMixer, (byte) 100);
    }

    public void setSequencer(DrumMachineSequencer sequencer) {
        this.sequencer = sequencer;
    }

    @Override
    public short[] getSoundChunk() {

        // todo Handle exceptions
        if (currentChunk == 1) {
            sequencer.advance();
            String[] samples = sequencer.getCurrentNoteSamples();
            try {
                drumMachineSoundProducer.setNotes(samples);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
        currentChunk++;
        if (currentChunk > CHUNKS_PER_NOTE) {
            currentChunk = 1;
        }

        short[] chunk = new short[BUFFER_SIZE];
        // todo Use FX Mixer
        System.arraycopy(outputMixer.getSoundChunk(), 0, chunk, 0, BUFFER_SIZE);
        return chunk;

    }
}
