package me.tropicalfan344.musicbot.commands.impl;

import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.effects.options.EffectOption;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandEffectsAdd<T extends AudioEffect> extends MusicCommand {

    private final T effectType;

    public CommandEffectsAdd(T effectType) {
        super(effectType.getId(), "Active a/an " + effectType.getDisplayName() + " effect.", effectType.getAllCommandsOptions().toArray(new OptionData[0]));
        this.effectType = effectType;
    }

    @Override
    @SneakyThrows
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        T e = (T) this.effectType.getClass().newInstance();
        for (EffectOption allOption : e.getAllOptions()) {
           allOption.setValue(allOption.extractValue(event.getInteraction()));
        }
        musicManager.getAudioEffectsManager().getEffects().add(e);
        event.getInteraction().replyEmbeds((SimpleEmbedGenerator.generateSuccessfulEmbed(
                "Successfully activated effect: " + e.getDisplayName()
        ))).queue();
    }
}
