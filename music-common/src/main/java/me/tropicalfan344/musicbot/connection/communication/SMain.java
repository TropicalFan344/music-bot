package me.tropicalfan344.musicbot.connection.communication;

import me.fan87.facket.Facket;
import me.fan87.facket.api.CommunicationClass;
import me.fan87.facket.api.annotations.FacketAsync;
import me.fan87.facket.api.server.FacketConnection;
import me.tropicalfan344.musicbot.connection.CTrack;

public class SMain extends CommunicationClass {
    private final FacketConnection connection;

    public SMain() {
        super();
        this.connection = null;
    }

    public SMain(Facket facket, FacketConnection connection) {
        super(facket);
        this.connection = connection;
    }

    @FacketAsync
    public void updateSong(CTrack track) {
        System.out.println("Update");
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            System.out.println(stackTraceElement);
        }
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
