package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;

public class CommandRickroll extends MusicCommand {
    public CommandRickroll() {
        super("rickroll", "Someone tell me to add this command");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        event.deferReply().queue();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        manager.add(YouTubeTrack.getVideoById("dQw4w9WgXcQ"));
        event.getHook().editOriginalEmbeds(SimpleEmbedGenerator.generateWarningEmbed("Rick roll coming!!!!!!"));
    }
}
