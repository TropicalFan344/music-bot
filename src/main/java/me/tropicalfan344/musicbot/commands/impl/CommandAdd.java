package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandAdd extends MusicCommand {
    public CommandAdd() {
        super("add", "Add a song to queue", new OptionData(OptionType.STRING, "query", "URL of the song / The song name", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        event.getInteraction().deferReply().queue();
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());

        Track targetTrack = musicManager.findSong(event.getOption("query").getAsString());
        musicManager.add(targetTrack);
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setTitle("Added")
                .setDescription("[" + targetTrack.getTitle() + "](" + targetTrack.getUrl() + ")")
                .setImage(targetTrack.getThumbnail())
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
    }
}
