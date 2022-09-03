package me.tropicalfan344.musicbot.commands;

import com.google.gson.JsonObject;
import me.tropicalfan344.musicbot.engines.Track;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SaveManager {

    final String guildId;
    public SaveManager(String guildId) throws IOException {
        this.guildId = guildId;
        File saves = new File("saves");
        File lists = new File(guildId + ".json");
        if(!saves.exists())saves.mkdir();
        if(!lists.exists())lists.createNewFile();
    }

    public void save(Track... tracks) {

    }

    public List<String> listSaves() {

    }

    public List<Track> load() {

    }

    public void delete() {

    }

}
