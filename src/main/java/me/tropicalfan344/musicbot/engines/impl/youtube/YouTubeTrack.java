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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class YouTubeTrack extends Track {

    @Getter
    private final String videoId;

    @SneakyThrows
    public static YouTubeTrack getByVideoId(String id) {
        JsonObject requestBody = new JsonObject();
        requestBody.add("context", YouTubeEngine.getContextIOS("en", "US"));
        requestBody.addProperty("videoId", id);


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
        JsonObject videoDetails = responseBody.getAsJsonObject("videoDetails");
        if (videoDetails == null) {
            throw new EngineException("YouTube Video with ID: " + id + " is not found!", "Expected videoDetails in root, but it's not found.");
        }
        String title = videoDetails.get("title").getAsString();
        String author = videoDetails.get("author").getAsString();
        JsonArray thumbnails = videoDetails.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
        String thumbnail = thumbnails.get(thumbnails.size() - 1).getAsJsonObject().get("url").getAsString();
        return new YouTubeTrack(title, author, thumbnail, id);

    }

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

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File outputFile = new File(tmpDir, UUID.randomUUID().toString() + ".musicbot.pcm");

        tmpDir.mkdirs();
        outputFile.createNewFile();
//        URL url = new URL(currentUrl);
//        URLConnection urlConnection = url.openConnection();
//        InputStream inputStream = urlConnection.getInputStream();
//        FileOutputStream outputStream = new FileOutputStream(outputFile);
//        byte[] readBuffer = new byte[1024];
//        while (true) {
//            int read = inputStream.read(readBuffer);
//            if (read == -1) break;
//            outputStream.write(readBuffer, 0, read);
//        }
//        outputStream.close();
//        inputStream.close();
        Process process = new ProcessBuilder("ffmpeg", "-i", currentUrl, "-y", "-ar", "48000", "-ac", "2", "-f", "s16be", "-acodec", "pcm_s16be", outputFile.getAbsolutePath()).start();
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


        return new InputStream() {
            InputStream targetStream = null;
            int currentIndex = 0;
            @Override
            public int read() throws IOException {
                currentIndex++;

                if (targetStream != null) {
                    int read = targetStream.read();
                    if (read != -1) {
                        return read;
                    }
                }
                int read = -1;
                while (read == -1 && process.isAlive()) {
                    if (targetStream != null) {
                        targetStream.close();
                    }
                    targetStream = new FileInputStream(outputFile);
                    for (int i = 0; i < currentIndex; i++) {
                        read = targetStream.read();
                        if (read == -1) {
                            break;
                        }
                    }
                }

                return read;
            }

            @Override
            public void close() throws IOException {
                try {
                    targetStream.close();
                } catch (Exception ignored) {}
                try {
                    process.destroy();
                } catch (Exception ignored) {}
            }
        };
    }

    @Override
    public List<Track> openRadio() {
        // RDMM
        return super.openRadio();
    }
}
