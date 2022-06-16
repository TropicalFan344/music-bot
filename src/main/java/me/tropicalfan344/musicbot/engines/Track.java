package me.tropicalfan344.musicbot.engines;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public abstract class Track {

    @Getter private final String title;
    @Getter private final String artist;
    @Getter private final String thumbnail;
    @Getter private final String url;
    @Getter private final int length;

    public Track(String title, String artist, String thumbnail, String url, int length) {
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
        this.url = url;
        this.length = length;
    }

    public abstract PCMInputStream getPCMStream(); // Not yet implemented / not implemented yet

    public List<Track> openRadio() {
        throw new RuntimeException("Engine's openRadio() has not been implemented yet");
    }

    public String getEmbedDisplay() {
        return "[" + getTitle() + "](" + getUrl() + ") by " + getArtist();
    }



}
