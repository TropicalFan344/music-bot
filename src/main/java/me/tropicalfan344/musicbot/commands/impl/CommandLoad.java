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

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CommandLoad extends MusicCommand {
    public CommandLoad() {
        super("load", "Load saved list", new OptionData(OptionType.STRING, "name", "name of list", true), new OptionData(OptionType.STRING, "guildid", "If you want to load the playlist from other guild, please enter the guild ID."));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();
        String guildId;
        if (event.getOption("guildid") != null) {
            guildId = event.getOption("guildid").getAsString();
        }else {
            guildId = event.getGuild().getId();
        }
        File list = new File("saves/" + guildId + ".json");
        Gson gson = new Gson();
        String name = event.getOption("name").getAsString();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());

        if (!list.exists() && guildId.equals(event.getGuild().getId())) {
            throw new CommandException("There is no saved list on this guild. Use /save to save current tracks");
        }else if (!list.exists() && !guildId.equals(event.getGuild().getId())){
            throw new CommandException("Target guild doesn't exist or no list on that guild");
        }

        File file = new File("saves/" + guildId + ".json");
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        JsonObject jsonList = gson.fromJson(reader, JsonObject.class);
        reader.close();

        if (!jsonList.has(name)) throw new CommandException("List not found, check your cases or create a new list by using /save");

        for (JsonElement song : jsonList.getAsJsonArray(name)) {
            String title = song.getAsJsonObject().get("title").getAsString();
            String artist = song.getAsJsonObject().get("artist").getAsString();
            String thumbnail = song.getAsJsonObject().get("thumbnail").getAsString();
            String videoId = song.getAsJsonObject().get("videoId").getAsString();
            int length = song.getAsJsonObject().get("length").getAsInt();
            manager.add(new YouTubeTrack(title, artist, thumbnail, videoId, length));
        }
        event.getHook().editOriginalEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("List **" + name + "** loaded! (" + jsonList.getAsJsonArray(name).size() + " songs)")).queue();
    }
}
