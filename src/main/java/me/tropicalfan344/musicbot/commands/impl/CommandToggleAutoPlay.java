package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;

public class CommandToggleAutoPlay extends MusicCommand {
    public CommandToggleAutoPlay() {
        super("tgautoplay", "Toggle AutoPlay");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        manager.setAutoPlay(!manager.isAutoPlay());
        if (manager.isAutoPlay()) {
            event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("AutoPlay is now **Enabled**")).queue();
        } else {
            event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("AutoPlay is now **Disabled**")).queue();
        }
    }
}
