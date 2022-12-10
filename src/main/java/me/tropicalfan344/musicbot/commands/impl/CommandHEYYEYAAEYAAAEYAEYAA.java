package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.io.IOException;

public class CommandHEYYEYAAEYAAAEYAEYAA extends MusicCommand {
    public CommandHEYYEYAAEYAAAEYAEYAA() {
        super("HEYYEYAAEYAAAEYAEYAA".toLowerCase(), "HEYYEYAAEYAAAEYAEYAA");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        GuildVoiceState selfVoiceState = event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (!event.getMember().getVoiceState().inAudioChannel()){
            throw new CommandException("You are not in a voice channel right now!");
        }
        if (selfVoiceState.inAudioChannel()) {
            manager.playMusic(YouTubeTrack.getVideoById("QEFWUIjyE9Y"));
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Color(0x5DFF51))
                    .setImage("https://i.imgur.com/cBJzJTH.gif")
                    .setTitle("HEYYEYAAEYAAAEYAEYAA")
                    .build()).queue();
        }else {
            manager.joinVoiceChannel(event.getMember().getVoiceState().getChannel());
        }
    }
}
