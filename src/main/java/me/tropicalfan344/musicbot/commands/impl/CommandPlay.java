package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.TrackSendHandler;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class CommandPlay extends MusicCommand {
    public CommandPlay() {
        super("play", "Add a song to the queue.", new OptionData(OptionType.STRING, "query", "URL of the song / The song name", true));
    }


    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (!event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState().inAudioChannel()) {
            throw new CommandException("I'm not in a voice channel, use /join to let me join your voice channel.");
        }else {
            event.getInteraction().deferReply().queue();
            GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
            Track targetTrack = musicManager.findSong(event.getOption("query").getAsString());
            musicManager.playMusic(targetTrack);
            event.getHook().editOriginalEmbeds(new EmbedBuilder()
                    .setTitle("Now Playing")
                    .setDescription("[" + targetTrack.getTitle() + "](" + targetTrack.getUrl() + ")")
                    .setImage(targetTrack.getThumbnail())
                    .setColor(SimpleEmbedGenerator.SUCCESS)
                    .build()).queue();
        }
    }
}
