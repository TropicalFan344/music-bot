package me.tropicalfan344.musicbot.engines.impl.youtube;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.engines.EngineException;
import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.JsonRequestBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

import static me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine.INNERTUBE_API_KEY;

public class YouTubeSearchResult implements ISearchResult {

    private String continuousToken;
    private String query;

    private final JsonObject responseBody;
    private final JsonArray contents;

    private final YouTubeEngine engine;

    @SneakyThrows
    public YouTubeSearchResult(YouTubeEngine engine, String continuousToken, String query) {
        this.engine = engine;
        this.continuousToken = continuousToken;
        this.query = query;

        JsonObject object = new JsonObject();
        if (continuousToken != null) {
            object.addProperty("continuation", continuousToken);
        }
        if (query != null) {
            object.addProperty("query", query);
        }
        // TODO: Configurable Language and region
        object.add("context", engine.getContextWeb("en", "US"));

        Request request = new Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/search?key=" + INNERTUBE_API_KEY)
                .post(new JsonRequestBody(object))
                .build();

        Response response = YouTubeEngine.okHttp.newCall(request).execute();
        String body = response.body().string();
        if (response.code() != 200) {
            throw new EngineException("Invalid response from YouTube server!",
                    "Response Code: " + response.code() + ", expected: 200. Body: " + body);
        }
        response.close();


        responseBody = YouTubeEngine.gson.fromJson(body, JsonObject.class);
        if (query != null) {
            contents = responseBody
                    .getAsJsonObject("contents")
                    .getAsJsonObject("twoColumnSearchResultsRenderer")
                    .getAsJsonObject("primaryContents")
                    .getAsJsonObject("sectionListRenderer")
                    .getAsJsonArray("contents");
        } else {
            contents = responseBody.getAsJsonArray("onResponseReceivedCommands").get(0)
                    .getAsJsonObject().getAsJsonObject("appendContinuationItemsAction")
                    .getAsJsonArray("continuationItems");
        }
    }


    @Override
    public List<Track> getResults() {
        List<Track> tracks = new ArrayList<>();

        for (JsonElement jsonElement : contents.get(0).getAsJsonObject().getAsJsonObject("itemSectionRenderer").getAsJsonArray("contents")) {
            JsonObject video = jsonElement.getAsJsonObject().getAsJsonObject("videoRenderer");
            if (video != null) {
                JsonArray thumbnails = video.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
                tracks.add(new YouTubeTrack(
                        YouTubeEngine.extractRuns(video.getAsJsonObject("title")),
                        YouTubeEngine.extractRuns(video.getAsJsonObject("ownerText")),
                        thumbnails.get(thumbnails.size() - 1).getAsJsonObject().get("url").getAsString(),
                        video.get("videoId").getAsString()
                ));
            }
        }
        return tracks;
    }

    @Override
    public ISearchResult nextPage() {

        String continuationToken;
        continuationToken = contents.get(1).getAsJsonObject()
                .getAsJsonObject("continuationItemRenderer")
                .getAsJsonObject("continuationEndpoint")
                .getAsJsonObject("continuationCommand")
                .get("token").getAsString();
        continuationToken = continuationToken.replace("%3D", "");

        return new YouTubeSearchResult(engine, continuationToken, null);
    }
}
