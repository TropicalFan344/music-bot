package me.tropicalfan344.musicbot.commands.impl;

import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandEffectsList extends MusicCommand {
    public CommandEffectsList() {
        super("list-available-effects", "List all effects that could be added.");
    }

    @Override
    @SneakyThrows
    public void onExecute(SlashCommandInteractionEvent event) {
        String out = "";
        for (Class<? extends AudioEffect> aClass : AudioEffectsManager.registry) {
            AudioEffect effect = aClass.newInstance();
            out += " - " + effect.getDisplayName() + " (Active via /" + effect.getId() + ")\n";
        }
        event.getInteraction().replyEmbeds(new EmbedBuilder()
                .setTitle("Available Effects")
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .setDescription(out)
                .build()).queue();
    }
}
