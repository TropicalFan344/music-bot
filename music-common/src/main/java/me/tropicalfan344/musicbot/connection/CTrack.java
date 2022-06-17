package me.tropicalfan344.musicbot.connection;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CTrack {

    public String title;
    public String artist;
    public String thumbnail;
    public String url;
    public int length;

    @Override
    public String toString() {
        return title + " by " + artist + " (" + url + ")";
    }
}
