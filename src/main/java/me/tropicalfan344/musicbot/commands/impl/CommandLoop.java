package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandLoop extends MusicCommand {
    public CommandLoop() {
        super("loop", "Change how the bot plays the music in loop",
                new OptionData(OptionType.STRING, "mode", "The loop mode", true)
                        .addChoices(Arrays.stream(GuildMusicManager.LoopMode.values()).map(loopMode -> new Command.Choice(loopMode.name().toLowerCase(Locale.ROOT), loopMode.name())).collect(Collectors.toList()))
        );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager.LoopMode targetLoopMode = GuildMusicManager.LoopMode.valueOf(event.getOption("mode").getAsString());

        GuildMusicManager.getMusicManager(musicBot, event.getGuild()).setLoopMode(targetLoopMode);

        event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed(targetLoopMode.getEmoji() + " Loop mode has been changed to " + targetLoopMode.getName() + "!")).queue();
    }
}
