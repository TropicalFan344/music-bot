package me.tropicalfan344.musicbot.engines.impl.youtube;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.engines.EngineException;
import me.tropicalfan344.musicbot.engines.PlayList;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.JsonRequestBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine.INNERTUBE_API_KEY;

public class YouTubePlayList extends PlayList {

    public static final Pattern ytInitialDataPattern = Pattern.compile("(?<=var ytInitialData = ).*(?=;<\\/script>)");

    private JsonArray contents;
    private final String listTitle;
    private final int size;
    private String continuation;
    private List<Track> track = new ArrayList<>();

    @SneakyThrows
    public YouTubePlayList(String playListId) {
        OkHttpClient client = new OkHttpClient();
        Response rawResponse = client.newCall(new Request.Builder()
                .url("https://www.youtube.com/playlist?list=" + playListId)
                .get()
                .build()).execute();
        String pageSource = rawResponse.body().string();
        rawResponse.close();
        Matcher matcher = ytInitialDataPattern.matcher(pageSource);
        long start = System.currentTimeMillis();
        if (!matcher.find()) {
            throw new EngineException("Something went wrong while reading playlist", "Could not find initial data in page source");
        }
        Gson gson = new Gson();

        String json = matcher.group();
//        System.out.println(json);
        JsonObject response = gson.fromJson(json, JsonObject.class);
//        System.out.println("Extracted Data! Took " + (System.currentTimeMillis() - start) + "ms");
        this.contents = response.getAsJsonObject("contents")
                .getAsJsonObject("twoColumnBrowseResultsRenderer")
                .getAsJsonArray("tabs").get(0).getAsJsonObject()
                .getAsJsonObject("tabRenderer")
                .getAsJsonObject("content")
                .getAsJsonObject("sectionListRenderer")
                .getAsJsonArray("contents").get(0).getAsJsonObject()
                .getAsJsonObject("itemSectionRenderer")
                .getAsJsonArray("contents").get(0).getAsJsonObject()
                .getAsJsonObject("playlistVideoListRenderer")
                .getAsJsonArray("contents");
        this.listTitle = response.getAsJsonObject("sidebar")
                .getAsJsonObject("playlistSidebarRenderer")
                .getAsJsonArray("items").get(0).getAsJsonObject()
                .getAsJsonObject("playlistSidebarPrimaryInfoRenderer")
                .getAsJsonObject("title")
                .getAsJsonArray("runs").get(0).getAsJsonObject()
                .get("text").getAsString();
        this.size = Integer.parseInt(response.getAsJsonObject("sidebar")
                .getAsJsonObject("playlistSidebarRenderer")
                .getAsJsonArray("items").get(0).getAsJsonObject()
                .getAsJsonObject("playlistSidebarPrimaryInfoRenderer")
                .getAsJsonArray("stats").get(0).getAsJsonObject()
                .getAsJsonArray("runs").get(0).getAsJsonObject()
                .get("text").getAsString());
        for (JsonElement content : contents) {
            if (content.getAsJsonObject().getAsJsonObject("playlistVideoRenderer") == null) {
                continuation = content.getAsJsonObject()
                        .getAsJsonObject("continuationItemRenderer")
                        .getAsJsonObject("continuationEndpoint")
                        .getAsJsonObject("continuationCommand")
                        .get("token").getAsString();
                continue;
            }
            JsonObject playlistVideoRenderer = content.getAsJsonObject().getAsJsonObject("playlistVideoRenderer");
            if (playlistVideoRenderer != null) {
                String title = playlistVideoRenderer.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject().get("text").getAsString();
                String artist = playlistVideoRenderer.getAsJsonObject("shortBylineText").getAsJsonArray("runs").get(0).getAsJsonObject().get("text").getAsString();
                String thumbnail = playlistVideoRenderer.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails").get(2).getAsJsonObject().get("url").getAsString();
                String videoId = playlistVideoRenderer.get("videoId").getAsString();
                int length = Integer.parseInt(playlistVideoRenderer.get("lengthSeconds").getAsString());
                track.add(new YouTubeTrack(title, artist, thumbnail, videoId, length));
            }
        }
    }


    @Override
    public String getTitle() {
        return this.listTitle;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public List<Track> getTracks() {
        return track;
    }

    @Override
    @SneakyThrows
    public @Nullable PlayList getNextPage() {
        track.clear();
        if (continuation != null) {
            JsonObject object = new JsonObject();
            object.addProperty("continuation", continuation);
            object.add("context", YouTubeEngine.getContextWeb("en", "US"));
            Request request = new Request.Builder()
                    .url("https://www.youtube.com/youtubei/v1/browse?key=" + INNERTUBE_API_KEY)
                    .post(new JsonRequestBody(object))
                    .build();
            Response response = YouTubeEngine.okHttp.newCall(request).execute();
            String body = response.body().string();
            JsonObject responseBody = YouTubeEngine.gson.fromJson(body, JsonObject.class);
            JsonArray contents = responseBody.getAsJsonArray("onResponseReceivedActions").get(0).getAsJsonObject()
                    .getAsJsonObject("appendContinuationItemsAction")
                    .getAsJsonArray("continuationItems");
            for (JsonElement content : contents) {
                JsonObject playlistVideoRenderer = content.getAsJsonObject().getAsJsonObject("playlistVideoRenderer");
                if (playlistVideoRenderer == null) {
                    String token = content.getAsJsonObject().getAsJsonObject("continuationItemRenderer").getAsJsonObject("continuationEndpoint").getAsJsonObject("continuationCommand").get("token").getAsString();
                    if (token != null) {
                        continuation = token;
                    }
                    continue;
                }
                String title = playlistVideoRenderer.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject().get("text").getAsString();
                String artist = playlistVideoRenderer.getAsJsonObject("shortBylineText").getAsJsonArray("runs").get(0).getAsJsonObject().get("text").getAsString();
                String thumbnail = playlistVideoRenderer.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails").get(1).getAsJsonObject().get("url").getAsString();
                String videoId = playlistVideoRenderer.get("videoId").getAsString();
                int length = Integer.parseInt(playlistVideoRenderer.get("lengthSeconds").getAsString());
                track.add(new YouTubeTrack(title, artist, thumbnail, videoId, length));
            }

            return this;
        }
        return null;
    }
}
