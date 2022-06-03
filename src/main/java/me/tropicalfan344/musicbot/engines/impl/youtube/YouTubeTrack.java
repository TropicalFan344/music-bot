package me.tropicalfan344.musicbot.engines.impl.youtube;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.engines.EngineException;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.JsonRequestBody;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YouTubeTrack extends Track {

    @Getter
    private final String videoId;

    public YouTubeTrack(String title, String artist, String thumbnail, String videoId) {
        super(title, artist, thumbnail, "https://www.youtube.com/watch?v=" + videoId);
        this.videoId = videoId;
    }

    @Override
    @SneakyThrows
    public InputStream getPCMStream() {
        JsonObject requestBody = new JsonObject();
        requestBody.add("context", YouTubeEngine.getContextIOS("en", "US"));
        requestBody.addProperty("videoId", videoId);


        Request request = new Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/player?key=" + YouTubeEngine.INNERTUBE_API_KEY)
                .post(new JsonRequestBody(requestBody))
                .build();
        Response response = YouTubeEngine.okHttp.newCall(request).execute();
        String body = response.body().string();
        if (response.code() != 200) {
            throw new EngineException("Invalid response from YouTube server!",
                    "Response Code: " + response.code() + ", expected: 200. Body: " + body);
        }
        JsonObject responseBody = YouTubeEngine.gson.fromJson(body, JsonObject.class);

        JsonArray adaptiveFormats = responseBody.getAsJsonObject("streamingData")
                .getAsJsonArray("adaptiveFormats");
        int currentMax = 0;
        String currentUrl = null;
        for (JsonElement element : adaptiveFormats) {
            JsonObject format = element.getAsJsonObject();
            String mimeType = format.get("mimeType").getAsString();
            if (mimeType.startsWith("audio/")) {
                int bitRate = format.get("bitrate").getAsInt();
                if (bitRate > currentMax) {
                    currentMax = bitRate;
                    currentUrl = format.get("url").getAsString();
                }
            }
        }
        System.out.println(currentUrl);
        Process process = new ProcessBuilder("ffmpeg", "-i", currentUrl, "-y", "-ar", "48000", "-ac", "2", "-f", "s16be", "-acodec", "pcm_s16be", "pipe:1").start();
        new Thread(() -> {
            try {
                InputStream errorStream = process.getErrorStream();
                while (true) {
                    int read = errorStream.read();
                    if (read == -1) break;
                    System.out.write(read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = process.getInputStream();
        while (true) {
            int read = inputStream.read();
            if (read == -1) break;
            outputStream.write(read);
        }
        inputStream.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    public List<Track> openRadio() {
        // RDMM
        return super.openRadio();
    }
}
