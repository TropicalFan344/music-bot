package me.tropicalfan344.musicbot.connection.communication;

import me.fan87.facket.Facket;
import me.fan87.facket.api.CommunicationClass;
import me.fan87.facket.api.server.FacketConnection;

import java.io.IOException;

public class CPackets extends CommunicationClass {
    private final FacketConnection connection;

    public CPackets() {
        super();
        this.connection = null;
    }

    public CPackets(Facket facket, FacketConnection connection) {
        super(facket);
        this.connection = connection;
    }

    public String link(String key) {
        return this.execute(connection, key);
    }

    @Override
    public Class<?> getBoundClass() {
        try {
            return Class.forName("me.tropicalfan344.musicbot.connection.communication.CPacketsImpl");
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public void heartbeat() throws IOException {
        this.execute(connection);
    }
}
