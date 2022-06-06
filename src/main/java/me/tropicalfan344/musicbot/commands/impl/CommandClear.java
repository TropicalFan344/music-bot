package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandClear extends MusicCommand {
    public CommandClear() {
        super("clear", "Clear the queue");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        musicManager.clearQueue();
        event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("Queue has been cleared")).queue();

    }
}
