package me.tropicalfan344.musicbot.commands.impl;

import com.google.gson.*;
import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CommandSave extends MusicCommand {
    public CommandSave() {
        super("save", "Save all songs in queue", new OptionData(OptionType.STRING, "name", "name of list", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event){
        event.getInteraction().deferReply().queue();
        File list = new File("saves/" + event.getGuild().getId() + ".json");
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonList;
        if (manager.getQueue().size() < 1) {
            throw new CommandException("There is nothing in queue");
        }
        if (!list.exists()) {
            try {
                list.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            jsonList = new JsonObject();
        }else {
            FileReader reader = null;
            try {
                reader = new FileReader("saves/" + event.getGuild().getId() + ".json");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            jsonList = gson.fromJson(reader, JsonObject.class);
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(jsonList.has(event.getOption("name").getAsString())) throw new CommandException("List already exists");
        JsonArray songList = new JsonArray();
        for (Track track : manager.getQueue()) {
            JsonObject song = new JsonObject();
            song.addProperty("title", track.getTitle());
            song.addProperty("artist", track.getArtist());
            song.addProperty("thumbnail", track.getThumbnail());
            song.addProperty("videoId", track.getUrl().replace("https://www.youtube.com/watch?v=", ""));
            song.addProperty("length", track.getLength());
            songList.add(song);
        }
        jsonList.add(event.getOption("name").getAsString(), songList);
        String output = gson.toJson(jsonList);
        FileOutputStream outputFile = null;
        try {
            outputFile = new FileOutputStream(list);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            outputFile.write(output.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        event.getHook().editOriginalEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("Successfully saved queue")).queue();
    }
}
