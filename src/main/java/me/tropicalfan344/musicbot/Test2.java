package me.tropicalfan344.musicbot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.engines.EngineException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test2 {
    public static final Pattern ytInitialDataPattern = Pattern.compile("(?<=var ytInitialData = ).*(?=;<\\/script>)");
    @SneakyThrows
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Response rawResponse = client.newCall(new Request.Builder()
                .url("https://www.youtube.com/playlist?list=PLhy8TB5U6n17R78U7usaLQfCC8nbnG8Nc")
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
        System.out.println(json);
        JsonObject response = gson.fromJson(json, JsonObject.class);
        System.out.println("Extracted Data! Took " + (System.currentTimeMillis() - start) + "ms");
        JsonArray contents = response.getAsJsonObject("contents")
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
        for (JsonElement content : contents) {
            JsonObject playlistVideoRenderer = content.getAsJsonObject().getAsJsonObject("playlistVideoRenderer");
            if (playlistVideoRenderer == null) {
                String continuation = "4qmFsgJhEiRWTFBMaHk4VEI1VTZuMTdSNzhVN3VzYUxRZkNDOG5ibkc4TmMaFENBRjZCbEJVT2tOSFVRJTNEJTNEmgIiUExoeThUQjVVNm4xN1I3OFU3dXNhTFFmQ0M4bmJuRzhOYw";
                continue;
            }
            String videoId = playlistVideoRenderer.get("videoId").getAsString();
            String title = playlistVideoRenderer.getAsJsonObject("title").getAsJsonArray("runs").get(0).getAsJsonObject().get("text").getAsString();
            System.out.println(title);
        }

    }
}
