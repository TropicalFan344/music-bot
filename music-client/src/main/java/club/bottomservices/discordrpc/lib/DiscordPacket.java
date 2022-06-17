package club.bottomservices.discordrpc.lib;

import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a packet of data in communication with discord
 */
public final class DiscordPacket {
    private final OpCode opCode;
    private final JsonObject json;

    public DiscordPacket(OpCode opCode, JsonObject json) {
        this.opCode = opCode;
        this.json = json;
    }

    public OpCode opCode() {
        return opCode;
    }

    public JsonObject json() {
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        DiscordPacket that = (DiscordPacket) obj;
        return Objects.equals(this.opCode, that.opCode) &&
                Objects.equals(this.json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opCode, json);
    }

    @Override
    public String toString() {
        return "DiscordPacket[" +
                "opCode=" + opCode + ", " +
                "json=" + json + ']';
    }

    /**
     * @return A byte array containing the contents of this packet
     */
    public byte[] toBytes() {
        byte[] jsonBytes = json.toString().getBytes(StandardCharsets.UTF_8);
        int jsonSize = jsonBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(jsonSize + 2 * Integer.BYTES);
        buffer.putInt(Integer.reverseBytes(opCode.ordinal()))
                .putInt(Integer.reverseBytes(jsonSize))
                .put(jsonBytes);
        return buffer.array();
    }

    /**
     * Opcodes used in communication with discord, the ordering of these is important
     */
    public enum OpCode {
        HANDSHAKE,
        MESSAGE,
        CLOSE
    }
}
