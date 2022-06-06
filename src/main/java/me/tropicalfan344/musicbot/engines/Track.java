package me.tropicalfan344.musicbot.engines;

import lombok.Getter;

import java.io.InputStream;
import java.util.List;

public abstract class Track {

    @Getter private final String title;
    @Getter private final String artist;
    @Getter private final String thumbnail;
    @Getter private final String url;

    public Track(String title, String artist, String thumbnail, String url) {
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
        this.url = url;
    }

    public abstract InputStream getPCMStream();

    public List<Track> openRadio() {
        throw new RuntimeException("Engine's openRadio() has not been implemented yet");
    }

    public String getEmbedDisplay() {
        return "[" + getTitle() + "](" + getUrl() + ")";
    }

}
