package me.tropicalfan344.musicbot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamSendHandler implements AudioSendHandler, Closeable {


    private boolean closed = false;

    @Getter
    @Setter
    private boolean paused = false;

    @Getter
    private final InputStream inputStream;
    private final GuildMusicManager musicManager;

    /**
     * Create an instance of stream send handler. Stream send handler reads the input stream, and send it back to JDA.
     * After sending back to JDA, JDA will encode the audio to Opus, and send it back to Discord.
     * The required audio data can be accessed via {@link AudioSendHandler#INPUT_FORMAT}
     * @param inputStream Raw input stream of PCM signed 16-bit big endian.
     */
    public StreamSendHandler(InputStream inputStream, GuildMusicManager musicManager) {
        this.inputStream = inputStream;
        this.musicManager = musicManager;
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
                int i1 = ((readBuffer[i] << 8) | (readBuffer[i+1] & 0xff)); // unsigned short
                float value = i1 / 0xffff;
                effectBuffer[i / 2] = value;
            }

            for (AudioEffect effect : musicManager.getAudioEffectsManager().getEffects()) {
                effect.process(effectBuffer);
            }

            for (int i = 0; i < effectBuffer.length; i++) {
                int i1 = (int) effectBuffer[i] * 0xffff;
                readBuffer[i+1] = ((byte) i1);
                readBuffer[i] = (byte) (i1 >> 8);
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
    public void close() throws IOException {
        inputStream.close();
        closed = true;
    }
}
