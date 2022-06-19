import club.bottomservices.discordrpc.lib.RichPresence;
import me.tropicalfan344.musicbot.connection.CTrack;
import me.tropicalfan344.musicbot.connection.MusicBotConstants;
import me.tropicalfan344.musicbot.connection.communication.SPackets;

public class SPacketsImpl extends SPackets {

    @Override
    public void heartbeat() {

    }

    @Override
    public void updateStatus(CTrack track) {
        System.out.println("Now Playing: " + track);


        if (track == null) {
            Main.client.sendPresence(new RichPresence(null, null, null, null, null, null, null));
            Main.client.sendPresence(new RichPresence.Builder()
                    .setText("Idle", null)
                    .setAssets("icon", "Implemented Protocol Version: " + MusicBotConstants.protocolVersion + ".0", null, null)
                    .addButton("What is this?", "https://gist.github.com/fan87/63cc826ecca0b581226c5c8b552d9695")
                    .build());
        } else {
            Main.client.sendPresence(new RichPresence.Builder()
                    .setText(track.title, "by " + track.artist)
                    .setAssets("icon", "Implemented Protocol Version: " + MusicBotConstants.protocolVersion + ".0", null, null)
                    .addButton("Listen on YouTube", track.url)
                    .addButton("What is this?", "https://gist.github.com/fan87/63cc826ecca0b581226c5c8b552d9695")
                    .build());
        }
    }

}
