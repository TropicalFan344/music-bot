package me.tropicalfan344.musicbot.engines.impl.youtube;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.engines.Engine;
import me.tropicalfan344.musicbot.engines.EngineException;
import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.JsonRequestBody;
import okhttp3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeEngine implements Engine {
    public static final String INNERTUBE_API_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";

    public static final String WEB_VERSION = "2.20220531.08.00";
    public static final String IOS_VERSION = "17.23.6";

    public static final OkHttpClient okHttp = new OkHttpClient.Builder()
            .build();

    public static final Gson gson = new Gson();

    @Override
    @SneakyThrows
    public ISearchResult search(String query) {
        return new YouTubeSearchResult(this, null, query);
    }
    public static final Pattern pattern = Pattern.compile("(?:https?:\\/\\/)?(?:\\w*\\.)?(?:(?:(?:youtube\\.com\\/)watch\\?v=)|(?:youtu\\.be\\/))([a-zA-Z0-9_-]{11})(?:(?:(?:&[\\w%]*=[\\w%]*)|(?:#[\\w%]*))*\\/?)*(?:&list=(PL\\w{32}))?(?:(?:(?:&[\\w%]*=[\\w%]*)|(?:#[\\w%]*))*\\/?)*");

    @Override
    public boolean canProvide(String url) {
        return url.matches(pattern.pattern());
//        return url.matches("https?:\\/\\/(?:\\w*\\.)?youtube.com\\/watch\\?v=([\\w\\n\\-]{11})(&[\\w%]*=[\\w%]*)*");
    }

    @Override
    public Track provide(String url) {
//        Pattern pattern = Pattern.compile("https?:\\/\\/(?:\\w*\\.)?youtube.com\\/watch\\?v=([\\w\\n\\-]{11})(&[\\w%]*=[\\w%]*)*");
        // (?:https?:\/\/)?(?:\w*\.)?(?:(?:(?:youtube\.com\/)watch\?v=)|(?:youtu\.be\/))([a-zA-Z0-9_-]{11})(?:\?list=(\w*))(?:(?:(?:&[\w%]*=[\w%]*)|(?:#[\w%]*))*\/?)*
        Matcher matcher = pattern.matcher(url);
        if (!matcher.find()) {
            throw new IllegalStateException("Track could not be provided by this engine with URL: " + url);
        }
        String videoId = matcher.group(1);
        String playlistId = matcher.group(2);
        return YouTubeTrack.getByVideoId(videoId);
    }

    public static JsonObject getContextIOS(String language, String location) {
        JsonObject out = new JsonObject();

        JsonObject client = new JsonObject();
        client: {
            client.addProperty("hl", language);
            client.addProperty("gl", location);
            client.addProperty("clientName", "IOS");
            client.addProperty("clientVersion", IOS_VERSION);
        }

        out.add("client", client);

        return out;
    }

    public static JsonObject getContextWeb(String language, String location) {
        JsonObject out = new JsonObject();

        JsonObject client = new JsonObject();
        client: {
            client.addProperty("hl", language);
            client.addProperty("gl", location);
            client.addProperty("clientName", "WEB");
            client.addProperty("clientVersion", WEB_VERSION);
        }

        out.add("client", client);

        return out;
    }

    public static String extractRuns(JsonObject run) {
        return run.getAsJsonArray("runs").get(0).getAsJsonObject().get("text").getAsString();
    }

}
