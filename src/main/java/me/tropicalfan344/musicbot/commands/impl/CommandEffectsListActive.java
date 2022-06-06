package me.tropicalfan344.musicbot.commands.impl;

import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandEffectsListActive extends MusicCommand {
    public CommandEffectsListActive() {
        super("list-effects", "List all effects that has been activated.");
    }

    @Override
    @SneakyThrows
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        String out = "";
        if (musicManager.getAudioEffectsManager().getEffects().isEmpty()) {
            out = "There's no activated effects";
        }
        for (AudioEffect effect : musicManager.getAudioEffectsManager().getEffects()) {
            out += " - " + effect.toString() + "\n";
        }
        event.getInteraction().replyEmbeds(new EmbedBuilder()
                .setTitle("Activated Effects")
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .setDescription(out)
                .build()).queue();
    }
}
