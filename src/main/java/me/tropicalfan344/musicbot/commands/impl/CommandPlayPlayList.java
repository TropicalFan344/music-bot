package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubePlayList;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandPlayPlayList extends MusicCommand {
    public CommandPlayPlayList() {
        super("playplaylist", "Play playlist with URL or playlistId", new OptionData(OptionType.STRING, "query", "URL or playlistId", true));
    }

    private static final Pattern pattern = Pattern.compile("(?:https?:\\/\\/)?(?:\\w*\\.)?youtube.com\\/playlist\\?list=([\\w\\n\\-]{32,})*");


    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        YouTubePlayList playList;
        event.getInteraction().deferReply().queue();
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (event.getOption("query").getAsString().startsWith("PL")) {
            playList = new YouTubePlayList(event.getOption("query").getAsString());
        }else {
            Matcher matcher = pattern.matcher(event.getOption("query").getAsString());
            matcher.find();
            playList = new YouTubePlayList(matcher.group(1));
        }
        List<Track> track = new ArrayList<>();
        for (Track playlistTrack : playList.getTracks()) {
            track.add(playlistTrack);
        }
        if (playList.getNextPage() != null) {
            for (Track playlistTrack : playList.getTracks()) {
                track.add(playlistTrack);
            }
        }
        for (Track track1 : track) {
            musicManager.add(track1);
        }
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setTitle("Now Playing")
                .setDescription("[" + musicManager.getQueue().get(0).getTitle() + "](" + musicManager.getQueue().get(0).getUrl() + ")")
                .setImage(musicManager.getQueue().get(0).getThumbnail())
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
    }
}
