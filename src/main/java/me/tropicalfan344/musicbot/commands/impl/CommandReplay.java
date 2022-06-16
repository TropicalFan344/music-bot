package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandReplay extends MusicCommand {
    public CommandReplay() {
        super("replay", "Replay the current song");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (musicManager.getQueue().isEmpty()) {
            throw new CommandException("Bot is not playing anything at the moment");
        }
        musicManager.replay();
        event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("\uD83D\uDD01 Replayed " + musicManager.getQueue().get(0).getEmbedDisplay())).queue();

    }
}
