package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandInsert extends MusicCommand {
    public CommandInsert() {
        super("insert", "Insert a song to the queue", new OptionData(OptionType.INTEGER, "index", "The index of the song to be inserted to", true), new OptionData(OptionType.STRING, "query", "URL of the song / The song name", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        event.getInteraction().deferReply().queue();
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());

        Track targetTrack = musicManager.findSong(event.getOption("query").getAsString());
        musicManager.insert(event.getOption("index").getAsInt(), targetTrack);
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setTitle("Inserted")
                .setDescription(targetTrack.getEmbedDisplay())
                .setImage(targetTrack.getThumbnail())
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
    }
}
