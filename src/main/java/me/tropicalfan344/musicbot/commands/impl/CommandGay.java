package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.io.IOException;

public class CommandGay extends MusicCommand {
    public CommandGay() {
        super("gay", "I am Gaaay");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        GuildVoiceState selfVoiceState = event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (!event.getMember().getVoiceState().inAudioChannel()){
            throw new CommandException("You are not in a voice channel right now!");
        }
        if (selfVoiceState.inAudioChannel()) {
            manager.playMusic(YouTubeTrack.getVideoById("03MkRR4eGNg"));
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(0x5DFF51))
                    .setImage("https://media.tenor.com/jOqCTOVBshQAAAAC/idubbbz-youtuber.gif")
                    .setTitle("I am Gaaay~~~")
                    .build()).queue();
        }else {
            manager.joinVoiceChannel(event.getMember().getVoiceState().getChannel());
            manager.playMusic(YouTubeTrack.getVideoById("QEFWUIjyE9Y"));
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(0x5DFF51))
                    .setImage("https://media.tenor.com/jOqCTOVBshQAAAAC/idubbbz-youtuber.gif")
                    .setTitle("I am Gaaay~~~")
                    .build()).queue();
        }
    }
}
