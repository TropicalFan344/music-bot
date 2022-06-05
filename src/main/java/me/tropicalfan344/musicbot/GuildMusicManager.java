package me.tropicalfan344.musicbot;

import lombok.Getter;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.engines.Track;
import net.dv8tion.jda.api.entities.Guild;

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



    @Getter private final List<Track> queue = new ArrayList<>();

    @Getter private final AudioEffectsManager audioEffectsManager;
    @Getter private final MusicBot musicBot;

    @Getter private final Guild guild;

    private StreamSendHandler sendHandler;

    public GuildMusicManager(MusicBot musicBot, Guild guild) {
        this.musicBot = musicBot;
        this.guild = guild;
        this.audioEffectsManager = new AudioEffectsManager(musicBot);
    }



}
