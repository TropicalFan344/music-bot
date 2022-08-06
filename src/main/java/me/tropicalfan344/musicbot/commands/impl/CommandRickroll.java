package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
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
        manager.joinVoiceChannel(event.getMember().getVoiceState().getChannel());
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setColor(new Color(0xff4747))
                        .setImage("https://tenor.com/view/rick-roll-gif-23595798")
                        .setTitle("Rick roll coming!!!!!")
                .build()).queue();
    }
}
