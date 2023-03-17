package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;

public class CommandFuckYou extends MusicCommand {
    public CommandFuckYou() {
        super("fuckyou", "Fuck you, Fuck you very very mu~~~ch! :middle_finger:");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        GuildVoiceState selfVoiceState = event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState();
        if (!event.getMember().getVoiceState().inAudioChannel()){
            throw new CommandException("You are not in a voice channel right now!");
        }

        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        musicManager.joinVoiceChannel(voiceChannel);
        musicManager.add(YouTubeTrack.getVideoById("RAkSkMRwPYs"));
        event.getInteraction().replyEmbeds(new EmbedBuilder()
                .setTitle("**Fuck you!**")
                .setImage("https://i1.sndcdn.com/artworks-000440697189-ew2fb6-t500x500.jpg")
                .setColor(SimpleEmbedGenerator.ERROR)
                .build()).queue();
    }
}
