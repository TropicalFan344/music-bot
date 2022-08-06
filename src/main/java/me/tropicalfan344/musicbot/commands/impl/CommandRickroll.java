package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.io.IOException;

public class CommandRickroll extends MusicCommand {
    public CommandRickroll() {
        super("rickroll", "Someone tell me to add this command");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        manager.add(YouTubeTrack.getVideoById("dQw4w9WgXcQ"));
        if (!event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState().inAudioChannel()) {
            GuildVoiceState selfVoiceState = event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState();
            if (!event.getMember().getVoiceState().inAudioChannel()){
                throw new CommandException("You are not in a voice channel right now!");
            }
            if (selfVoiceState.inAudioChannel()) {
                throw new CommandException("The bot is already in a voice channel! Please use /leave before letting it join another.");
            }

            VoiceChannel voiceChannel = ((VoiceChannel) event.getMember().getVoiceState().getChannel());
            GuildMusicManager.getMusicManager(musicBot, event.getGuild()).joinVoiceChannel(voiceChannel);
        }
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setColor(new Color(0xff4747))
                        .setImage("https://tenor.com/view/rick-roll-gif-23595798")
                        .setTitle("Rick roll coming!!!!!")
                .build()).queue();
    }
}
