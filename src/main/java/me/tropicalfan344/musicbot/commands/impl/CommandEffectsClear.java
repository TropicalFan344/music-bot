package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandEffectsClear extends MusicCommand {
    public CommandEffectsClear() {
        super("clear-effects", "Clear all effects in active");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        musicManager.getAudioEffectsManager().getEffects().clear();
        event.getInteraction().replyEmbeds((SimpleEmbedGenerator.generateSuccessfulEmbed(
                "Successfully cleared all effects"
        ))).queue();
    }
}
