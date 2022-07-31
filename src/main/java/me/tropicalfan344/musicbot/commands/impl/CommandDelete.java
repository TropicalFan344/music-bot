package me.tropicalfan344.musicbot.commands.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CommandDelete extends MusicCommand {
    public CommandDelete() {
        super("delete", "Delete saved list", new OptionData(OptionType.STRING, "name", "name of list", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();
        File list = new File("saves/" + event.getGuild().getId() + ".json");
        Gson gson = new Gson();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        String name = event.getOption("name").getAsString();

        if (!list.exists()) throw new CommandException("There is no saved list on this server. Use /save to save the list");

        FileReader reader = new FileReader("saves/" + event.getGuild().getId() + ".json");
        JsonObject jsonList = gson.fromJson(reader, JsonObject.class);
        reader.close();

        if (!jsonList.has(name)) throw new CommandException("List not found. Please check your cases");

        jsonList.remove(name);
        String output = gson.toJson(jsonList);
        FileOutputStream outputFile = null;
        outputFile = new FileOutputStream(list);
        outputFile.write(output.getBytes(StandardCharsets.UTF_8));
        outputFile.close();
        event.getHook().editOriginalEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("List deleted")).queue();
    }
}
