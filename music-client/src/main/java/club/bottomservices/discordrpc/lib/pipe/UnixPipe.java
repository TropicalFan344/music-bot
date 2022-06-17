package club.bottomservices.discordrpc.lib.pipe;

import club.bottomservices.discordrpc.lib.DiscordPacket;
import club.bottomservices.discordrpc.lib.DiscordRPCClient;
import club.bottomservices.discordrpc.lib.exceptions.NoDiscordException;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class UnixPipe implements Pipe {
    private final AFUNIXSocket socket;

    @SneakyThrows
    public UnixPipe() {
        Logger logger = LoggerFactory.getLogger(UnixPipe.class);
        this.socket = AFUNIXSocket.newInstance();

        String[] locations = {"XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP"};
        String location = null;

        for (String possible : locations) {
            location = System.getenv(possible);
            if (location != null) {
                break;
            }
        }

        if (location == null) {
            location = "/tmp";
        }

        for (int i = 0; i < 10; i++) {
            File file = new File(location + "/discord-ipc-" + i);
            if (file.exists()) {
                try {
                    socket.connect(AFUNIXSocketAddress.of(file));
                    break;
                } catch (IOException e) {
                    logger.info("IOException while binding socket " + i, e);
                }
            }
        }

        if (!socket.isConnected()) {
            throw new NoDiscordException("Discord client not found");
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        socket.getOutputStream().write(data);
    }

    @Nonnull
    @Override
    public DiscordPacket read() throws IOException {
        byte[] headerBuffer = new byte[Integer.BYTES * 2];
        socket.getInputStream().read(headerBuffer);
        ByteBuffer header = ByteBuffer.wrap(headerBuffer);

        DiscordPacket.OpCode opCode = DiscordPacket.OpCode.values()[Integer.reverseBytes(header.getInt())];
        int size = Integer.reverseBytes(header.getInt());

        byte[] payloadBuffer = new byte[size];
        socket.getInputStream().read(payloadBuffer);
        return new DiscordPacket(opCode, DiscordRPCClient.GSON.fromJson(new String(payloadBuffer), JsonObject.class));
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
