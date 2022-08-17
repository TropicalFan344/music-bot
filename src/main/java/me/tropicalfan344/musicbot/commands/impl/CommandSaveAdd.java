package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.io.IOException;

public class CommandSaveAdd extends MusicCommand {
    public CommandSaveAdd() {
        super("save-add", "Add song to specified list", new OptionData(OptionType.STRING, "name", "name of list", true), new OptionData(OptionType.STRING, "query", "URL of the song / The song name", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.getInteraction().deferReply().queue();
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        Track targetTrack = musicManager.findSong(event.getOption("query").getAsString());
        File list =  new File("saves/" + event.getGuild().getId() + ".json");

    }
}
