import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubePlayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class YouTubeAPITest {

    public static final Pattern pattern = Pattern.compile("(?:https?:\\/\\/)?(?:\\w*\\.)?(?:(?:(?:youtube\\.com\\/)watch\\?v=)|(?:youtu\\.be\\/))([a-zA-Z0-9_-]{11})(?:(?:(?:&[\\w%]*=[\\w%]*)|(?:#[\\w%]*))*\\/?)*(?:&list=(PL\\w{32}))+(?:(?:(?:&[\\w%]*=[\\w%]*)|(?:#[\\w%]*))*\\/?)*");


    @Test
    public void playListTest() {
        YouTubePlayList playlist = new YouTubePlayList("PLkhhqm0fX3g94C1QST8MISTZubRvvcDfn");
        List<Track> track = new ArrayList<>();
        for (Track playlistTrack : playlist.getTracks()) {
            track.add(playlistTrack);
        }
        if (playlist.getNextPage() != null) {
            for (Track playlistTrack : playlist.getTracks()) {
                track.add(playlistTrack);
            }
        }
        for (Track track1 : track) {
            System.out.println(track1.getTitle());
        }
    }

}
