package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandSkip extends MusicCommand {
    public CommandSkip() {
        super("skip", "Skip the music that's playing", new OptionData(OptionType.INTEGER, "index", "index of the song you want skip to"));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (musicManager.getQueue().isEmpty()) {
            throw new CommandException("Couldn't skip anything");
        }
        int amount = 1;
        if (event.getOption("index") != null) {
            amount = event.getOption("index").getAsInt() - 1;
        }
        Track skippedTrack = musicManager.getQueue().get(0);
        int skip = musicManager.skip(amount);
        if (amount == 1) {
            event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("⏭ Skipped " + skippedTrack.getEmbedDisplay())).queue();
        } else {
            event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("⏭ Skipped " + skip + " songs")).queue();
        }

    }
}
