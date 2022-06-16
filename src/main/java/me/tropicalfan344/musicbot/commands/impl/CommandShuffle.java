package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandShuffle extends MusicCommand {
    public CommandShuffle() {
        super("shuffle", "Shuffle the queue (except the first track)");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (musicManager.getQueue().size() <= 2) {
            throw new CommandException("Nothing to shuffle!");
        }
        musicManager.shuffle();
        event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("\uD83D\uDD00 Shuffled " + (musicManager.getQueue().size() - 1) + " songs!")).queue();

    }
}
