package me.tropicalfan344.musicbot.commands.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CommandLoad extends MusicCommand {
    public CommandLoad() {
        super("load", "Load saved list", new OptionData(OptionType.STRING, "name", "name of list", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();
        File list = new File("saves/" + event.getGuild().getId() + ".json");
        Gson gson = new Gson();
        String name = event.getOption("name").getAsString();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (!list.exists()) new CommandException("There is no saved list on the server. Use /save to save the list");
        JsonObject jsonList = gson.fromJson(new FileReader("saves/" + event.getGuild().getId() + ".json"), JsonObject.class);
        if (!jsonList.has(name)) new CommandException("List not found, check your cases or create a new list by using /save");
        for (JsonElement song : jsonList.getAsJsonArray(name)) {
            String title = song.getAsJsonObject().get("title").getAsString();
            String artist = song.getAsJsonObject().get("artist").getAsString();
            String thumbnail = song.getAsJsonObject().get("thumbnail").getAsString();
            String videoId = song.getAsJsonObject().get("videoId").getAsString();
            int length = song.getAsJsonObject().get("length").getAsInt();
            manager.add(new YouTubeTrack(title, artist, thumbnail, videoId, length));
        }
        event.getHook().editOriginalEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("List **" + name + "** loaded!")).queue();
    }
}
