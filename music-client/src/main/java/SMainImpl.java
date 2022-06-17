import club.bottomservices.discordrpc.lib.RichPresence;
import me.tropicalfan344.musicbot.connection.CTrack;
import me.tropicalfan344.musicbot.connection.communication.SMain;

public class SMainImpl extends SMain {

    @Override
    public void updateSong(CTrack track) {
        System.out.println("Now Playing: " + track);


        if (track == null) {
            Main.client.sendPresence(new RichPresence(null, null, null, null, null, null, null));
            Main.client.sendPresence(new RichPresence.Builder()
                    .setText("Idle", null)
                    .setAssets("icon", "PRBB Styled Music Bot", null, null)
                    .build());
        } else {
            Main.client.sendPresence(new RichPresence.Builder()
                    .setText(track.title, "by " + track.artist)
                    .setAssets("icon", "PRBB Styled Music Bot", null, null)
                    .addButton("Listen on YouTube", track.url)
                    .build());
        }
    }

}
