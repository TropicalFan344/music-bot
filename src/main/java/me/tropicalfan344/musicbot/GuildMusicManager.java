package me.tropicalfan344.musicbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.connection.CTrack;
import me.tropicalfan344.musicbot.connection.ConnectedClient;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.engines.Track;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.net.SocketException;
import java.util.*;

public class GuildMusicManager {

    private static final Map<String, GuildMusicManager> musicManagers = new HashMap<>();


    private static boolean initalized = false;
    public static GuildMusicManager getMusicManager(MusicBot musicBot, Guild guild) {
        if (!initalized) {
            musicBot.getJda().addEventListener(new ListenerAdapter() {
                @Override
                public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
                    GuildMusicManager musicManager = getMusicManager(musicBot, event.getGuild());
                    AudioManager guildAudioManager = musicManager.getGuildAudioManager();
                    AudioChannel connectedChannel = guildAudioManager.getConnectedChannel();
                    if (connectedChannel == event.getChannelJoined()) {
                        musicManager.updateRPC(event.getMember().getUser());
                    }
                }

                @Override
                public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
                    GuildMusicManager musicManager = getMusicManager(musicBot, event.getGuild());
                    AudioManager guildAudioManager = musicManager.getGuildAudioManager();
                    AudioChannel connectedChannel = guildAudioManager.getConnectedChannel();
                    if (connectedChannel == event.getChannelJoined()) {
                        musicManager.updateRPC(event.getMember().getUser());
                    } else if (connectedChannel == event.getChannelLeft()) {
                        for (ConnectedClient client : musicBot.getConnectionManager().getClientMap().values()) {
                            if (client.getLinkedUser() == null) {
                                continue;
                            }
                            if (client.getLinkedUser().getIdLong() == event.getMember().getUser().getIdLong()) {
                                try {
                                    client.getCommunicationClass().updateSong(null);
                                } catch (Exception e) {}
                            }
                            return;
                        }
                    }
                }

                @Override
                public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
                    GuildMusicManager musicManager = getMusicManager(musicBot, event.getGuild());
                    AudioManager guildAudioManager = musicManager.getGuildAudioManager();
                    AudioChannel connectedChannel = guildAudioManager.getConnectedChannel();
                    if (connectedChannel == event.getChannelLeft()) {
                        for (ConnectedClient client : musicBot.getConnectionManager().getClientMap().values()) {
                            if (client.getLinkedUser() == null) {
                                continue;
                            }
                            if (client.getLinkedUser().getIdLong() == event.getMember().getUser().getIdLong()) {
                                try {
                                    client.getCommunicationClass().updateSong(null);
                                } catch (Exception e) {}
                            }
                        }
                    }
                }
            });
            initalized = true;
        }

        GuildMusicManager cached = musicManagers.get(guild.getId());
        if (cached == null) {
            cached = new GuildMusicManager(musicBot, guild);
            musicManagers.put(guild.getId(), cached);
        }
        return cached;
    }



    private final List<Track> queue = new ArrayList<>();

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

    public void updateRPC(User user) {
        if (getGuildAudioManager().getConnectedChannel() != null) {
            for (ConnectedClient client : musicBot.getConnectionManager().getClientMap().values()) {
                if (client.getLinkedUser() == null) {
                    continue;
                }
                if (client.getLinkedUser().getIdLong() == user.getIdLong()) {
                    if (getQueue().size() >= 1) {
                        Track nowPlaying = getQueue().get(0);
                        try {
                            client.getCommunicationClass().updateSong(new CTrack(nowPlaying.getTitle(), nowPlaying.getArtist(), nowPlaying.getThumbnail(), nowPlaying.getUrl(), nowPlaying.getLength()));
                        } catch (Exception e) {}
                    } else {
                        try {
                            client.getCommunicationClass().updateSong(null);
                        } catch (Exception e) {}
                    }
                    return;
                }
            }
        }
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
        if (queue.isEmpty()) {
            return false;
        }
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
        return true;
    }

    public void refreshQueue(boolean force) {
        if (force) {
            if (getGuildAudioManager().getConnectedChannel() != null) {
                for (ConnectedClient client : musicBot.getConnectionManager().getClientMap().values()) {
                    if (client.getLinkedUser() == null) {
                        continue;
                    }
                    if (getGuildAudioManager().getConnectedChannel().getMembers().stream().anyMatch(member -> member.getUser().getIdLong() == client.getLinkedUser().getIdLong())) {
                        try {
                            client.getCommunicationClass().updateSong(null);
                        } catch (Exception e) {}
                    }
                }
            }
            sendHandler.closeWithoutRefreshingThisStupidGodDamnFuckingPieceOfShit();
            sendHandler = null;
        }
        if (sendHandler == null) {
            if (!queue.isEmpty()) {
                Track nowPlaying = queue.get(0);

                if (getGuildAudioManager().getConnectedChannel() != null) {
                    for (ConnectedClient client : musicBot.getConnectionManager().getClientMap().values()) {
                        if (client.getLinkedUser() == null) {
                            continue;
                        }
                        if (getGuildAudioManager().getConnectedChannel().getMembers().stream().anyMatch(member -> member.getUser().getIdLong() == client.getLinkedUser().getIdLong())) {
                            try {
                                client.getCommunicationClass().updateSong(new CTrack(nowPlaying.getTitle(), nowPlaying.getArtist(), nowPlaying.getThumbnail(), nowPlaying.getUrl(), nowPlaying.getLength()));
                            } catch (Exception ignored) {}
                        }
                    }
                }

                sendHandler = new TrackSendHandler(queue.get(0), this, () -> {
                    nextSong(false);
                    refreshQueue(true);
                });
                getGuildAudioManager().setSendingHandler(sendHandler);
            }
        } else {
            if (queue.isEmpty() || sendHandler.getTrack() != queue.get(0)) {
                if (getGuildAudioManager().getConnectedChannel() != null) {
                    for (ConnectedClient client : musicBot.getConnectionManager().getClientMap().values()) {
                        if (client.getLinkedUser() == null) {
                            continue;
                        }
                        if (getGuildAudioManager().getConnectedChannel().getMembers().stream().anyMatch(member -> member.getUser().getIdLong() == client.getLinkedUser().getIdLong())) {
                            try {
                                client.getCommunicationClass().updateSong(null);
                            } catch (Exception e) {}
                        }
                    }
                }
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
        Track track = queue.get(from);
        queue.add(to, track);
        if (to > from) {
            queue.remove(from);
        } else {
            queue.remove(from + 1);
        }
        refreshQueue(false);
    }

    public void insert(@Range(from = 0, to = Long.MAX_VALUE) int index, Track track) {
        queue.add(Math.min(index, Math.max(0, queue.size() - 1)), track);
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
