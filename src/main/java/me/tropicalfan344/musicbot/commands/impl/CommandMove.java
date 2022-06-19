package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandMove extends MusicCommand {
    public CommandMove() {
        super("move", "Move a song in the queue",
                new OptionData(OptionType.INTEGER, "source", "Index of the song to be moved (Starts from 1)", true),
                new OptionData(OptionType.INTEGER, "dest", "New index of the song. (Starts from 1)", true)
        );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        int from = event.getOption("source").getAsInt();
        int to = event.getOption("dest").getAsInt();
        if (from > manager.getQueue().size()) {
            throw new CommandException("There are only " + manager.getQueue().size() + " song(s) in queue, but you've entered " + from + " (from)");
        }
        if (to > manager.getQueue().size()) {
            throw new CommandException("There are only " + manager.getQueue().size() + " song(s) in queue, but you've entered " + to + " (to)");
        }
        if (from == to) {
            throw new CommandException("The source is equal to the dest. (" + from + " = " + to + ")");
        }
        manager.move(from - 1, to - 1);
        Track track = manager.getQueue().get(to - 1);
        event.getInteraction().replyEmbeds(new EmbedBuilder()
                .setTitle("Moved")
                .setDescription(track.getEmbedDisplay())
                .setImage(track.getThumbnail())
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
    }
}
