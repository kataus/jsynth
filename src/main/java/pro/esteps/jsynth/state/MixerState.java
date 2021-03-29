package pro.esteps.jsynth.state;

public class MixerState {

    private byte[][] channelBuffers = new byte[4][2048];

    public void setChannelBuffer(int channel, byte[] data) {
        System.arraycopy(data, 0, channelBuffers[channel], 0, channelBuffers[channel].length);
    }

    public byte[][] getChannelBuffers() {
        byte[][] copy = new byte[4][2048];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(channelBuffers[i], 0, copy[i], 0, 2048);
        }
        return copy;
    }

}
