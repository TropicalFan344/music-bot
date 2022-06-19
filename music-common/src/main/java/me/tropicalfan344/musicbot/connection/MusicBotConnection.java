package me.tropicalfan344.musicbot.connection;

import me.fan87.facket.Facket;
import me.tropicalfan344.musicbot.connection.communication.CPackets;
import me.tropicalfan344.musicbot.connection.communication.SPackets;

public class MusicBotConnection {

    public static void init(Facket facket) {
        facket.registerCommunicationClass(CPackets.class);
        facket.registerCommunicationClass(SPackets.class);
    }

}
