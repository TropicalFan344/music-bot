package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
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
        if (!event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState().inAudioChannel()) {
                                                            throw new CommandException("I'm not in a voice channel, use /join to let me join your voice channel.");
        } else {
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
            int size = 0;
            Track sex = null;
            for (Track playlistTrack : playList.getTracks()) {
                if (sex == null) {
                    sex = playlistTrack;
                }
                musicManager.addWithoutRefresh(playlistTrack);
                size++;
            }
            if (playList.getNextPage() != null) {
                for (Track playlistTrack : playList.getTracks()) {
                    if (sex == null) {
                        sex = playlistTrack;
                    }
                    musicManager.addWithoutRefresh(playlistTrack);
                    size++;
                }
            }

            musicManager.refreshQueue(false);
            event.getHook().editOriginalEmbeds(new EmbedBuilder()
                    .setTitle("Song(s) have been Added")
                    .setDescription("Added playlist " + playList.getTitle() + " (" + size + ") to the queue")
                    .setImage(sex.getThumbnail())
                    .setColor(SimpleEmbedGenerator.SUCCESS)
                    .build()).queue();
        }
    }
}
