package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.connection.MusicBotConstants;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class CommandStats extends MusicCommand {
    public CommandStats() {
        super("stats", "[Debug] Show the stats of the bot");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (musicManager.getQueue().isEmpty()) {
            throw new CommandException("Bot is not playing anything at the moment");
        }
        event.getInteraction().replyEmbeds(new EmbedBuilder()
                .setTitle("Debug Info")
                .addField("Download Speed", Math.round(musicManager.getSendHandler().getInputStream().downloadedLastSecond/1024d/1024d*100d)/100d + "MB/s", true)
                .addField("Protocol Version", MusicBotConstants.protocolVersion + "", true)
                .setColor(new Color(0x2bb1ff))
                .build()).queue();

    }
}
