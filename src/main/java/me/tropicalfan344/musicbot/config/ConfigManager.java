package me.tropicalfan344.musicbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    public ConfigManager() {
        reloadConfig();
    }

    @Getter
    private Config config;

    @SneakyThrows
    public void reloadConfig() {
        File configFile = new File("config.json");


        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        this.config = gson.fromJson(new FileReader(configFile), Config.class);
        if (this.config == null) {
            this.config = new Config();
            String s = gson.toJson(this.config);
            FileOutputStream outputStream = new FileOutputStream(configFile);
            outputStream.write(s.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }
    }

}
