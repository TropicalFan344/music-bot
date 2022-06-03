package me.tropicalfan344.musicbot.utils;

import com.google.gson.JsonElement;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonRequestBody extends RequestBody {

    private final JsonElement element;

    public JsonRequestBody(JsonElement element) {
        this.element = element;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.get("application/json");
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {

        bufferedSink.write(this.element.toString().getBytes(StandardCharsets.UTF_8));
        bufferedSink.close();

    }
}
