package me.tropicalfan344.musicbot.commands.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandSaveList extends MusicCommand {
    public CommandSaveList() {
        super("list-saves", "Show all saves on this server");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.getInteraction().deferReply().queue();
        File list = new File("saves/" + event.getGuild().getId() + ".json");
        Gson gson = new Gson();

        if (!list.exists()) throw new CommandException("There is no saved list on this server. Use /save to save the list");
        InputStreamReader reader = new InputStreamReader(new FileInputStream("saves/" + event.getGuild().getId() + ".json"), "UTF-8");
        JsonObject jsonList = gson.fromJson(reader, JsonObject.class);
        reader.close();

        String output = "";
        for (int i = 0; i < jsonList.keySet().size();) {
            for (String s : jsonList.keySet()) {
                output += i+1 + ". " + s + " (" + jsonList.get(s).getAsJsonArray().size() + ")\n";
                i++;
            }
        }
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setColor(new Color(0x5DFF51))
                .setTitle("Save(s):")
                .setDescription(output)
                .build()).queue();
    }
}
