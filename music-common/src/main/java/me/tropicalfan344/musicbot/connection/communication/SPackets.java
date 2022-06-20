package me.tropicalfan344.musicbot.connection.communication;

import me.fan87.facket.Facket;
import me.fan87.facket.api.CommunicationClass;
import me.fan87.facket.api.annotations.FacketAsync;
import me.fan87.facket.api.server.FacketConnection;
import me.tropicalfan344.musicbot.connection.CTrack;

public class SPackets extends CommunicationClass {
    private final FacketConnection connection;

    public SPackets() {
        super();
        this.connection = null;
    }

    public SPackets(Facket facket, FacketConnection connection) {
        super(facket);
        this.connection = connection;
    }

    public void heartbeat() {
        this.execute(connection);
    }

    @FacketAsync
    public void updateStatus(CTrack track) {
        this.execute(connection, track);
    }


    @Override
    public Class<?> getBoundClass() {
        try {
            return Class.forName("SMainImpl");
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}
