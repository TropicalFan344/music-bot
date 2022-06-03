package me.tropicalfan344.musicbot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.commands.impl.CommandPlay;
import me.tropicalfan344.musicbot.commands.impl.CommandQueue;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamSendHander implements AudioSendHandler, Closeable {

    @Setter
    @Getter
    public static boolean done = false;

    @Setter
    @Getter
    public static boolean paused = false;

    @Getter
    private final InputStream inputStream;

    /**
     * Create an instance of stream send handler. Stream send handler reads the input stream, and send it back to JDA.
     * After sending back to JDA, JDA will encode the audio to Opus, and send it back to Discord.
     * The required audio data can be accessed via {@link AudioSendHandler#INPUT_FORMAT}
     * @param inputStream Raw input stream of PCM signed 16-bit big endian.
     */
    public StreamSendHander(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public boolean canProvide() {
        System.out.println(done + ", " + paused);
        return !done && !paused;
    }

    public static int volume = 1000;


    private final ByteBuffer buffer = ByteBuffer.allocate((int) (INPUT_FORMAT.getFrameRate() * 0.02) * INPUT_FORMAT.getFrameSize());
    private final byte[] readBuffer = new byte[buffer.capacity()];


    @Nullable
    @SneakyThrows
    @Override
    public ByteBuffer provide20MsAudio() {
        if (CommandPlay.played) {
            int read = inputStream.read(readBuffer);
            if (read == -1) {
                CommandPlay.played = false;
                close();
                return null;
            }
            for (int i = 0; i < readBuffer.length; i+=2) {
                int i1 = (int) ((readBuffer[i] << 8) | (readBuffer[i+1] & 0xff));
//                float fuck = i1/Float.MAX_VALUE;
//                fuck *= 0.3;
//                fuck = Math.max(Math.min(fuck, 1), -1);
                i1*=volume*0.001;
                readBuffer[i+1] = ((byte) i1);
                readBuffer[i] = (byte) (i1 >> 8);
//                if (old != readBuffer[i+1]) {
//                    System.out.println(readBuffer[i+1] + "/" + old + "/" + i1 + "/" + (old << 8));
//                }

            }
            buffer.put(readBuffer);
            buffer.flip();
            return buffer;
        }else {
            return null;
        }
    }

    public boolean isClosed() {
        return done;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        done = true;
    }
}
