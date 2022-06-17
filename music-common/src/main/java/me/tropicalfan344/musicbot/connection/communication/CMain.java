package me.tropicalfan344.musicbot.connection.communication;

import me.fan87.facket.Facket;
import me.fan87.facket.api.CommunicationClass;
import me.fan87.facket.api.server.FacketConnection;

public class CMain extends CommunicationClass {
    private final FacketConnection connection;

    public CMain() {
        super();
        this.connection = null;
    }

    public CMain(Facket facket, FacketConnection connection) {
        super(facket);
        this.connection = connection;
    }

    public String link(String key) {
        return this.execute(connection, key);
    }

    @Override
    public Class<?> getBoundClass() {
        try {
            return Class.forName("me.tropicalfan344.musicbot.connection.communication.CMainImpl");
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}
