package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandResume extends MusicCommand {
    public CommandResume() {
        super("resume", "Resume the music");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (!musicManager.isPaused()) {
            throw new CommandException("The music has not been paused yet");
        }else {
            musicManager.resume();
            event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("▶ Music has been resumed")).queue();
        }
    }
}
