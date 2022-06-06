package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class CommandEffectsRemove extends MusicCommand {
    public CommandEffectsRemove() {
        super("remove-effect", "Remove a single effect by its index", new OptionData(OptionType.INTEGER, "index", "Index of the effect (Default: 1) (Range: 1 - inf)"));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        List<AudioEffect> effects = musicManager.getAudioEffectsManager().getEffects();
        int indexNumber = event.getInteraction().getOption("index").getAsInt();
        if (effects.isEmpty()) {
            throw new CommandException("There's no active effects right now");
        }
        if (indexNumber > effects.size()) {
            throw new CommandException("There are only " + effects.size() + " effects available, but you've entered " + indexNumber);
        }
        AudioEffect removedEffect = effects.remove(indexNumber - 1);
        event.getInteraction().replyEmbeds((SimpleEmbedGenerator.generateSuccessfulEmbed(
                "Successfully removed effect: " + removedEffect
        ))).queue();
    }
}
