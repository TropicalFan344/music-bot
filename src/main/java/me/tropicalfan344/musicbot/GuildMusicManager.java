package me.tropicalfan344.musicbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.connection.CTrack;
import me.tropicalfan344.musicbot.connection.ConnectedClient;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.engines.Track;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

public class GuildMusicManager {

    private static final Map<String, GuildMusicManager> musicManagers = new HashMap<>();


    public static GuildMusicManager getMusicManager(MusicBot musicBot, Guild guild) {
        GuildMusicManager cached = musicManagers.get(guild.getId());
        if (cached == null) {
            cached = new GuildMusicManager(musicBot, guild);
            musicManagers.put(guild.getId(), cached);
        }
        return cached;
    }



    private final List<Track> queue = new ArrayList<>();

    @Getter private Track lastTrack = null;

    @Getter @Setter private int index = 0;

    @Getter private List<Track> lastTrackOP = new ArrayList<>();

    @Getter @Setter private boolean isAutoPlay = false;

    @Getter @Setter private long downloadSpeed;

    @Getter private final AudioEffectsManager audioEffectsManager;
    @Getter private final MusicBot musicBot;

    @Getter private final Guild guild;

    private TrackSendHandler sendHandler;


    @Getter @Setter private LoopMode loopMode = LoopMode.OFF;

    public GuildMusicManager(MusicBot musicBot, Guild guild) {
        this.musicBot = musicBot;
        this.guild = guild;
        this.audioEffectsManager = new AudioEffectsManager(musicBot);


    }

    public AudioManager getGuildAudioManager() {
        return getGuild().getAudioManager();
    }

    public TrackSendHandler getSendHandler() {
        if (!(getGuildAudioManager().getSendingHandler() instanceof TrackSendHandler)) {
            return null;
        }
        return ((TrackSendHandler) getGuildAudioManager().getSendingHandler());
    }

    /**
     * @param skip If it's skipping, and it's in "repeat one" mode, it will still skip it
     */
    private boolean nextSong(boolean skip) {
        lastTrack = getQueue().get(getQueue().size()-1);
        if (queue.isEmpty()) {
            return false;
        }
        if (loopMode == LoopMode.OFF) {
            if(index < queue.size()-1) {
                index++;
            }
        }
        if (loopMode == LoopMode.SINGLE) {

        }
        if (loopMode == LoopMode.ALL) {
            index = index % queue.size();
        }
        return true;
    }

    public void refreshQueue(boolean force) {
        if (force) {
            sendHandler.closeWithoutRefreshingThisStupidGodDamnFuckingPieceOfShit();
            sendHandler = null;
        }
        if (sendHandler == null) {
            if (!queue.isEmpty()) {
                Track nowPlaying = queue.get(index);


                sendHandler = new TrackSendHandler(nowPlaying, this, () -> {
                    nextSong(false);
                    refreshQueue(true);
                });
                getGuildAudioManager().setSendingHandler(sendHandler);
            }
        } else {
            if (queue.isEmpty() || sendHandler.getTrack() != queue.get(index)) {
                sendHandler.closeWithoutRefreshingThisStupidGodDamnFuckingPieceOfShit();
                sendHandler = null;
                refreshQueue(false);
                getGuildAudioManager().setSendingHandler(sendHandler);
            }
        }
        if(index == queue.size()-1 && isAutoPlay && loopMode == LoopMode.ALL) {
            lastTrackOP.addAll(lastTrack.openRadio());
            lastTrackOP.remove(0);
            queue.addAll(lastTrackOP);
            refreshQueue(false);
        }else {
            lastTrack = null;
        }
    }

    public void remove(@Range(from = 0, to = Long.MAX_VALUE) int index) {
        queue.remove(Math.min(index, queue.size() - 1));
        if (index < this.index) {
            this.index--;
        }
        lastTrack = getQueue().get(getQueue().size()-1);
        refreshQueue(false);
    }

    public void clearQueue() {
        queue.clear();
        refreshQueue(false);
        isAutoPlay = false;
        lastTrack = null;
    }

    public void move(int from, int to) {
        if (from >= queue.size()) {
            throw new IndexOutOfBoundsException("There are only " + queue.size() + " song(s) in queue, but you've entered " + from + " (from)");
        }
        if (to >= queue.size()) {
            throw new IndexOutOfBoundsException("There are only " + queue.size() + " song(s) in queue, but you've entered " + to + " (to)");
        }
        if (from == to) {
            throw new IllegalArgumentException("The source is equal to the dest. (" + from + " = " + to + ")");
        }
        Track track = queue.remove(from);
        queue.add(to, track);
        lastTrack = getQueue().get(getQueue().size()-1);
        refreshQueue(false);
    }

    public void insert(@Range(from = 0, to = Long.MAX_VALUE) int index, Track track) {
        queue.add(Math.min(index, Math.max(0, queue.size() - 1)), track);
        lastTrack = getQueue().get(getQueue().size()-1);
        refreshQueue(false);
    }

    public void playMusic(Track track) {
        insert(0, track);
    }

    public int skip(int amount) {
        int totalAmount = 0;
        for (int i = 0; i < amount; i++) {
            if (nextSong(true)) {
                totalAmount++;
            } else {
                break;
            }
        }
        refreshQueue(true);
        return totalAmount;
    }

    public void skip() {
        nextSong(true);
        refreshQueue(true);
    }

    public void seek(int second) {
        if (second < this.getQueue().get(index).getLength()) {
            sendHandler.seek(second);
        }else {
            throw new CommandException("Hey, you can't go there because it doesn't exist in this song.");
        }
    }


    public void previous() {
        index = Math.max(0, index - 1);
        refreshQueue(true);
    }

    public void stop() {
        if (sendHandler != null) {
            sendHandler.close();
        }
    }

    public void pause() {
        if (sendHandler != null) {
            sendHandler.setPaused(true);
        }
    }

    public void resume() {
        if (sendHandler != null) {
            sendHandler.setPaused(false);
        }
    }

    public void joinVoiceChannel(AudioChannel channel) {
        getGuildAudioManager().setSelfMuted(true);
        getGuildAudioManager().openAudioConnection(channel);
    }

    public void disconnectFromVoiceChannel() {
        getGuildAudioManager().closeAudioConnection();
        clearQueue();
    }

    public boolean isPaused() {
        if (sendHandler == null) {
            return false;
        }
        return sendHandler.isPaused();
    }

    public Track findSong(String urlOrQuery) {
        // TODO: Multi-engine support
        if (musicBot.getEngine().canProvide(urlOrQuery)) {
            return musicBot.getEngine().provide(urlOrQuery);
        }
        List<Track> results = musicBot.getEngine().search(urlOrQuery).getResults();
        if (results.isEmpty()) {
            throw new IndexOutOfBoundsException("Could not find any result");
        }
        return results.get(0); // TODO: Selectable Support
    }

    public void addWithoutRefresh(Track track) {
        queue.add(track);
        lastTrack = getQueue().get(getQueue().size()-1);
    }

    public void add(Track track) {
        queue.add(track);
        lastTrack = getQueue().get(getQueue().size()-1);
        refreshQueue(false);
    }

    public void replay() {
        refreshQueue(true);
    }
    public void shuffle() {
        Track first = queue.remove(0);
        Collections.shuffle(queue);
        queue.add(0, first);
        lastTrack = getQueue().get(getQueue().size()-1);
    }

    public List<Track> getQueue() {
        return new ArrayList<>(queue);
    }

    @AllArgsConstructor
    @Getter
    public enum LoopMode {
        SINGLE("Single", "\uD83D\uDD02"),
        ALL("All", "\uD83D\uDD01"),
        OFF("Off", "❌️");

        String name;
        String emoji;
    }

}
