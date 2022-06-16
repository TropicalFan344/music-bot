package me.tropicalfan344.musicbot.engines.impl.youtube;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.engines.EngineException;
import me.tropicalfan344.musicbot.engines.PCMInputStream;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.JsonRequestBody;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        int length = videoDetails.get("lengthSeconds").getAsInt();
        JsonArray thumbnails = videoDetails.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
        String thumbnail = thumbnails.get(thumbnails.size() - 1).getAsJsonObject().get("url").getAsString();
        return new YouTubeTrack(title, author, thumbnail, id, length);

    }

    public YouTubeTrack(String title, String artist, String thumbnail, String videoId, int length) {
        super(title, artist, thumbnail, "https://www.youtube.com/watch?v=" + videoId, length);
        this.videoId = videoId;

    }

    @Override
    @SneakyThrows
    public PCMInputStream getPCMStream() {
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

//        System.out.println(body);

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

        Process process = new ProcessBuilder("ffmpeg", "-i", "pipe:", "-y", "-ar", "48000", "-ac", "2", "-f", "s16be", "-acodec", "pcm_s16be", outputFile.getAbsolutePath()).start();
        System.out.println("ffmpeg -i pipe: -y -ar 48000 -ac 2 -f s16be -acodec pcm_s16be pipe:1");

        new Thread(() -> {
            try {
                InputStream errorStream = process.getErrorStream();
                while (true) {
                    int read = errorStream.read();
                    if (read == -1) break;
                    System.err.write(read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        URL url = new URL(currentUrl);
        Pattern pattern = Pattern.compile("clen=(\\d*)&");
        Matcher matcher = pattern.matcher(url.getQuery());
        if (!matcher.find()) {
            throw new EngineException("Could not find total length in url!", "URL: " + url);
        }
        int length = Integer.parseInt(matcher.group(1));
        String finalCurrentUrl = currentUrl;
        byte[] buffer = new byte[1024*8];



        PCMInputStream pcmInputStream = new PCMInputStream() {

            InputStream targetStream = null;
            long currentIndex = 0;

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
            public void close() {
                try {
                    targetStream.close();
                    outputFile.delete();
                } catch (Exception ignored) {
                }
                try {
                    process.destroy();
                } catch (Exception ignored) {
                }
            }

            @Override
            public long skip(long amount) {
                currentIndex = currentIndex + amount;
                return 0;
            }
        };

        new Thread() {
            long dataPushTime = 0;
            long downloaded = 0;
            @Override
            @SneakyThrows
            public void run() {
                int readIndex = 0;
                OutputStream outputStream = process.getOutputStream();
                while (true) {
                    int remainingBytes = length - readIndex;
                    if (remainingBytes <= 0) {
                        break;
                    }
                    int bytesToBeRead = Math.min(32767, remainingBytes);
                    URL sendUrl = new URL(finalCurrentUrl + "&range=" + readIndex + "-" + (readIndex + bytesToBeRead - 1));
                    readIndex += bytesToBeRead;
                    InputStream inputStream = sendUrl.openStream();
                    while (true) {
                        int read = inputStream.read(buffer);
                        if (read == -1) break;
                        outputStream.write(buffer, 0, read);
                        if (System.currentTimeMillis() - dataPushTime > 1000) {
                            pcmInputStream.downloadedLastSecond = downloaded;
                            downloaded = 0;
                            dataPushTime = System.currentTimeMillis();
                        }
                        downloaded += read;
                    }
                    inputStream.close();
                }
                pcmInputStream.downloadedLastSecond = 0;
                outputStream.close();
            }
        }.start();
        return pcmInputStream;
    }

    @Override
    public List<Track> openRadio() {
        // RDMM
        return super.openRadio();
    }
}
