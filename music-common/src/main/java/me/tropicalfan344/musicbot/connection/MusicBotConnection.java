package me.tropicalfan344.musicbot.connection;

import me.fan87.facket.Facket;
import me.tropicalfan344.musicbot.connection.communication.CMain;
import me.tropicalfan344.musicbot.connection.communication.SMain;

public class MusicBotConnection {

    public static void init(Facket facket) {
        facket.registerCommunicationClass(CMain.class);
        facket.registerCommunicationClass(SMain.class);
    }

}
