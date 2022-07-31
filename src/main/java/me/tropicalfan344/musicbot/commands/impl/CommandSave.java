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
        super("save", "Save all songs in queue", new OptionData(OptionType.STRING, "name", "name of list", true), new OptionData(OptionType.BOOLEAN, "replace", "replace target list or not", false));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.getInteraction().deferReply().queue();
        File list = new File("saves/" + event.getGuild().getId() + ".json");
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonList;
        if (manager.getQueue().size() < 1) {
            throw new CommandException("There is nothing in queue");
        }
        if (!list.exists()) {
            list.createNewFile();
            jsonList = new JsonObject();
        }else {
            FileReader reader = null;
            reader = new FileReader("saves/" + event.getGuild().getId() + ".json");
            jsonList = gson.fromJson(reader, JsonObject.class);
            reader.close();
        }
        if (jsonList.has(event.getOption("name").getAsString()) && !    event.getOption("replace").getAsBoolean()) {
            throw new CommandException("List already exists");
        }
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
        outputFile = new FileOutputStream(list);
        outputFile.write(output.getBytes(StandardCharsets.UTF_8));
        outputFile.close();
        event.getHook().editOriginalEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("Queue saved (" + manager.getQueue().size() + " songs)")).queue();
    }
}
