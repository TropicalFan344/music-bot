package me.tropicalfan344.musicbot.connection.communication;

import me.tropicalfan344.musicbot.MusicBot;
import me.tropicalfan344.musicbot.connection.ConnectedClient;
import me.tropicalfan344.musicbot.connection.ConnectionManager;
import net.dv8tion.jda.api.entities.User;

public class CPacketsImpl extends CPackets {

    public static MusicBot bot;

    public ConnectedClient getBoundClient() {
        return MusicBot.instance.getConnectionManager().getClientMap().get(sender);
    }
    public User getBoundUser() {
        return getBoundClient().getLinkedUser();
    }

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
