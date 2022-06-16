package me.tropicalfan344.musicbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.engines.Track;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
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
    private void nextSong(boolean skip) {
        if (loopMode == LoopMode.OFF) {
            queue.remove(0); // Remove the first one
        }
        if (loopMode == LoopMode.SINGLE) {
            if (skip) {
                queue.add(queue.remove(0)); // Remove the first one, and add it to the bottom of the queue
            } else {
                // Do nothing so it will play the same fucking song
            }
        }
        if (loopMode == LoopMode.ALL) {
            queue.add(queue.remove(0)); // Remove the first one, and add it to the bottom of the queue
        }
    }

    public void refreshQueue(boolean force) {
        if (force) {
            sendHandler.closeWithoutRefreshingThisStupidGodDamnFuckingPieceOfShit();
            sendHandler = null;
        }
        if (sendHandler == null) {
            if (!queue.isEmpty()) {
                sendHandler = new TrackSendHandler(queue.get(0), this, () -> {
                    nextSong(false);
                    refreshQueue(true);
                });
                getGuildAudioManager().setSendingHandler(sendHandler);
            }
        } else {
            if (queue.isEmpty() || sendHandler.getTrack() != queue.get(0)) {
                sendHandler.closeWithoutRefreshingThisStupidGodDamnFuckingPieceOfShit();
                sendHandler = null;
                refreshQueue(false);
                getGuildAudioManager().setSendingHandler(sendHandler);
            }
        }
    }

    public void remove(@Range(from = 0, to = Long.MAX_VALUE) int index) {
        queue.remove(Math.min(index, queue.size() - 1));
        refreshQueue(false);
    }

    public void clearQueue() {
        queue.clear();
        refreshQueue(false);
    }

    public void insert(@Range(from = 0, to = Long.MAX_VALUE) int index, Track track) {
        queue.add(Math.min(index, Math.max(0, queue.size() - 1)), track);
        refreshQueue(false);
    }

    public void playMusic(Track track) {
        insert(0, track);
    }

    public void skip() {
        nextSong(true);
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
    }

    public void add(Track track) {
        queue.add(track);
        refreshQueue(false);
    }

    public void replay() {
        refreshQueue(true);
    }
    public void shuffle() {
        Track first = queue.remove(0);
        Collections.shuffle(queue);
        queue.add(0, first);
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
