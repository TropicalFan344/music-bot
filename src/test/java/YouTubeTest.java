import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;

public class YouTubeTest {

    public static void main(String[] args) {
        YouTubeEngine engine = new YouTubeEngine();
        ISearchResult result = engine.search("Laur");
        for (Track track : result.getResults()) {
            System.out.println("Found: " + track.getTitle() + " (" + track.getUrl() + ")");
        }
        while (true) {
            System.out.println("Going to the next page...");
            result = result.nextPage();
            for (Track track : result.getResults()) {
                System.out.println("Found: " + track.getTitle() + " (" + track.getUrl() + ")");
            }
        }
    }

}
