package me.tropicalfan344.musicbot.commands.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SaveContentButtonListener extends ListenerAdapter{

    private final CommandSaveContent commandSaveContent;

    public SaveContentButtonListener(CommandSaveContent commandSaveContent) {
        this.commandSaveContent = commandSaveContent;
    }

    @SneakyThrows
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
        if (event.getButton().getId().matches("saveGoPage:\\d+:\\w+")) {
            String[] split = event.getButton().getId().split(":");
            int page = Integer.parseInt(split[1]);
            File list = new File("saves/" + event.getGuild().getId() + ".json");
            Gson gson = new Gson();
            String name = split[2];

            FileReader reader = new FileReader("saves/" + event.getGuild().getId() + ".json");
            JsonObject jsonList = gson.fromJson(reader, JsonObject.class);
            reader.close();

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

            builder.setTitle("Songs in list: " + name);
            int requestedPage = page;
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
            builder.setDescription(out);
            builder.setColor(SimpleEmbedGenerator.SUCCESS);
            MessageEditCallbackAction reply = event.editMessage(new MessageBuilder(builder).build());
            List<Button> buttons = new ArrayList<>();

            if (requestedPage > 1) {
                buttons.add(Button.primary("saveGoPage:" + (requestedPage - 1) + ":" + name, "Previous Page"));
            }
            if (requestedPage < totalPages) {
                buttons.add(Button.primary("saveGoPage:" + (requestedPage + 1) + ":" + name, "Next Page"));
            }
            reply.setActionRow(buttons.toArray(new Button[0]));
            reply.queue();
        }
    }
}
