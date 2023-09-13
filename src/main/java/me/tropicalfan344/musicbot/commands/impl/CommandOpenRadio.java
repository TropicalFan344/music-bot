package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandOpenRadio extends MusicCommand {
    public CommandOpenRadio() {
        super("openradio", "Open a mix provided by the service");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        int index = musicManager.getIndex();
        Track nowPlaying = musicManager.getQueue().get(index);
        if (nowPlaying == null) {
            throw new CommandException("There is nothing in the queue");
        }
        List<Track> tracks = new ArrayList<>(nowPlaying.openRadio());
        for (Track track : tracks.subList(1, tracks.size())) {
            musicManager.add(track);
        }
        String des = "";
        int i = 1;
        for (Track track : tracks) {
            des += i + ". " + track.getEmbedDisplay() + "\n";
            i++;
        }
        event.getInteraction().replyEmbeds(new EmbedBuilder()
                .setTitle("Added " + (tracks.size()-1) +  " song(s) to queue")
                .setDescription(des)
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
    }
}
