package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandRemove extends MusicCommand {
    public CommandRemove() {
        super("remove", "Remove song from queue", new OptionData(OptionType.INTEGER, "index", "Index of the queue. Use /queue to check.", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        event.getInteraction().deferReply().queue();
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        Track targetTrack = manager.getQueue().get(event.getOption("index").getAsInt() - 1);
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setTitle("Removed")
                .setDescription("[" + targetTrack.getTitle() + "](" + targetTrack.getUrl() + ")")
                .setImage(targetTrack.getThumbnail())
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
        manager.remove(event.getOption("index").getAsInt() - 1);
    }
}
