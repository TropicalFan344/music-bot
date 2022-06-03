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

public class YouTubeEngine implements Engine {
    public static final String INNERTUBE_API_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";

    public static final String WEB_VERSION = "2.20220531.08.00";
    public static final String IOS_VERSION = "17.21.3";

    public static final OkHttpClient okHttp = new OkHttpClient.Builder()
            .build();

    public static final Gson gson = new Gson();

    @Override
    @SneakyThrows
    public ISearchResult search(String query) {
        return new YouTubeSearchResult(this, null, query);
    }

    @Override
    public boolean canProvide(String url) {
        return url.matches("https?:\\/\\/(?:\\w*\\.)?youtube.com\\/watch\\?v=([\\w\\n\\-]{11})(&[\\w%]*=[\\w%]*)*");
    }

    @Override
    public Track provide(String url) {
        return null;
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
