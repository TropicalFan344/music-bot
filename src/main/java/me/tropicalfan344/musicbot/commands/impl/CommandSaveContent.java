package me.tropicalfan344.musicbot.commands.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandSaveContent extends MusicCommand {

    @Override
    public void init() {
        musicBot.getJda().addEventListener(new SaveContentButtonListener(this));
    }
    public CommandSaveContent() {
        super("save-content", "Show all songs in target save", new OptionData(OptionType.STRING, "name", "Name of list", true), new OptionData(OptionType.INTEGER, "page", "The page number"));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        File list = new File("saves/" + event.getGuild().getId() + ".json");
        Gson gson = new Gson();
        String name = event.getOption("name").getAsString();

        if (!list.exists()) throw new CommandException("There is no saved list on this server. Use /save to save the list");

        InputStreamReader reader = new InputStreamReader(new FileInputStream("saves/" + event.getGuild().getId() + ".json"), "UTF-8");
        JsonObject jsonList = gson.fromJson(reader, JsonObject.class);
        reader.close();

        if (!jsonList.has(name)) throw new CommandException("List not found, check your cases or create a new list by using /save");

        JsonArray saveList = jsonList.getAsJsonArray(name);

        List<Track> tracks = new ArrayList<>();
        String out = "";
        EmbedBuilder builder = new EmbedBuilder();

        for (JsonElement song : saveList) {
            String title = song.getAsJsonObject().get("title").getAsString();
            String artist = song.getAsJsonObject().get("artist").getAsString();
            String thumbnail = song.getAsJsonObject().get("thumbnail").getAsString();
            String videoId = song.getAsJsonObject().get("videoId").getAsString();
            int length = song.getAsJsonObject().get("length").getAsInt();
            tracks.add(new YouTubeTrack(title, artist, thumbnail, videoId, length));
        }

        int requestedPage;

        if(event.getOption("page") != null) {
            requestedPage = event.getOption("page").getAsInt();
        }else {
            requestedPage = 1;
        }
        int pageAmount = 15;
        int totalPages = (int) Math.ceil(tracks.size() * 1.0 / pageAmount);
        requestedPage = Math.min(Math.max(requestedPage, 1), totalPages);
        int startIndex = (requestedPage - 1) * pageAmount; // Inclusive
        int endIndex = Math.min(tracks.size(), requestedPage * pageAmount); // Exclusive
        for (int i = startIndex; i < endIndex; i++) {
            Track track = tracks.get(i);
            out += (i + 1) + ". " + track.getEmbedDisplay();
            out += "\n";
        }
        builder.setTitle("Songs in list: " + name);
        builder.setDescription(out);
        builder.setColor(SimpleEmbedGenerator.SUCCESS);
        ReplyCallbackAction reply = event.getInteraction().replyEmbeds(builder.build());
        List<Button> buttons = new ArrayList<>();

        if (requestedPage > 1) {
            reply.addActionRow(Button.primary("saveGoPage:" + (requestedPage - 1) + ":" + name, "Previous Page"));
        }
        if (requestedPage < totalPages) {
            reply.addActionRow(Button.primary("saveGoPage:" + (requestedPage + 1) + ":" + name, "Next Page"));
        }
        reply.queue();
    }

}
