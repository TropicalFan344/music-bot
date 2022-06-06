package me.tropicalfan344.musicbot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.engines.Track;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class TrackSendHandler implements AudioSendHandler, Closeable {


    private boolean closed = false;

    @Getter
    @Setter
    private boolean paused = false;

    @Getter
    private final Track track;

    @Getter
    private final InputStream inputStream;
    private final GuildMusicManager musicManager;

    private final Runnable onFinish;

    /**
     * Create an instance of stream send handler. Stream send handler reads the input stream, and send it back to JDA.
     * After sending back to JDA, JDA will encode the audio to Opus, and send it back to Discord.
     * The required audio data can be accessed via {@link AudioSendHandler#INPUT_FORMAT}
     * @param inputStream Raw input stream of PCM signed 16-bit big endian.
     */
    public TrackSendHandler(Track track, GuildMusicManager musicManager, Runnable onFinish) {
        this.track = track;
        this.inputStream = track.getPCMStream();
        this.musicManager = musicManager;
        this.onFinish = onFinish;
    }

    @Override
    public boolean canProvide() {
        return !paused;
    }

    private final ByteBuffer buffer = ByteBuffer.allocate((int) (INPUT_FORMAT.getFrameRate() * 0.02) * INPUT_FORMAT.getFrameSize());
    private final byte[] readBuffer = new byte[buffer.capacity()];
    private final float[] effectBuffer = new float[buffer.capacity() / 2];


    @Nullable
    @SneakyThrows
    @Override
    public ByteBuffer provide20MsAudio() {
        if (!isClosed()) {
            int read = inputStream.read(readBuffer);
            if (read == -1) {
                close();
                return null;
            }
            for (int i = 0; i < readBuffer.length; i+=2) {
                short i1 = (short) ((readBuffer[i] << 8) | (readBuffer[i+1] & 0xff)); // signed short
                float data = i1 / 32767.0f;
                if (data > 1) data = 1;
                if (data < -1) data = -1;
                effectBuffer[i / 2] = data;
            }

            for (AudioEffect effect : musicManager.getAudioEffectsManager().getEffects()) {
                effect.processAudio(effectBuffer);
            }

            for (int i = 0; i < effectBuffer.length; i++) {
                float data = effectBuffer[i];
                if (data > 1) data = 1;
                if (data < -1) data = -1;
                int i1 = (int) (data * 32767.0);
                readBuffer[(i*2)+1] = ((byte) i1);
                readBuffer[i*2] = (byte) (i1 >> 8);
            }
            buffer.put(readBuffer);
            buffer.flip();
            return buffer;
        }else {
            return null;
        }
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    @SneakyThrows
    public void close() {
        inputStream.close();
        closed = true;
        onFinish.run();
    }
}
