package me.tropicalfan344.musicbot.connection.communication;

import me.tropicalfan344.musicbot.MusicBot;
import me.tropicalfan344.musicbot.connection.ConnectedClient;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class CMainImpl extends CMain {

    public static MusicBot bot;

    @Override
    public String link(String key) {
        User user = bot.getConnectionManager().link(sender, key);

        if (user == null) {
            return null;
        } else {
            System.out.println("[RPC] User linked: " + user.getAsTag() + " to " + sender.getSocket().getInetAddress());
            return "Successfully linked to " + user.getAsTag() + "   Please rejoin the voice channel to update the RPC!";
        }
    }

    @Override
    public void heartbeat() {

    }
}
