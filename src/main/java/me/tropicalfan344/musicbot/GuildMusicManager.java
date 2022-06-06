package me.tropicalfan344.musicbot;

import lombok.Getter;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.engines.Track;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public GuildMusicManager(MusicBot musicBot, Guild guild) {
        this.musicBot = musicBot;
        this.guild = guild;
        this.audioEffectsManager = new AudioEffectsManager(musicBot);
    }

    public AudioManager getGuildAudioManager() {
        return getGuild().getAudioManager();
    }

    /**
     * @param skip If it's skipping, and it's in "repeat one" mode, it will still skip it
     */
    private void nextSong(boolean skip) {
        queue.remove(0); // TODO: Repeat One, Repeat All, and Single (Implemented)
    }

    public void refreshQueue() {
        if (sendHandler == null) {
            if (!queue.isEmpty()) {
                sendHandler = new TrackSendHandler(queue.get(0), this, () -> {
                    nextSong(false);
                    refreshQueue();
                });
                getGuildAudioManager().setSendingHandler(sendHandler);
            }
        } else {
            if (queue.isEmpty() || sendHandler.getTrack() != queue.get(0)) {
                sendHandler = null;
                refreshQueue();
                getGuildAudioManager().setSendingHandler(sendHandler);
            }
        }
    }

    public void remove(@Range(from = 0, to = Long.MAX_VALUE) int index) {
        queue.remove(Math.min(index, queue.size() - 1));
        refreshQueue();
    }

    public void clearQueue() {
        queue.clear();
        refreshQueue();
    }

    public void insert(@Range(from = 0, to = Long.MAX_VALUE) int index, Track track) {
        queue.add(Math.min(index, Math.max(0, queue.size() - 1)), track);
        refreshQueue();
    }

    public void playMusic(Track track) {
        insert(0, track);
    }

    public void skip() {
        nextSong(true);
        refreshQueue();
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

    public void add(Track track) {
        queue.add(track);
        refreshQueue();
    }

    public List<Track> getQueue() {
        return new ArrayList<>(queue);
    }

}
