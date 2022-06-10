package me.tropicalfan344.musicbot.engines;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PlayList {

    public abstract String getTitle();

    public abstract int getSize();

    public abstract List<Track> getTracks();

    @Nullable
    public abstract PlayList getNextPage();

}
