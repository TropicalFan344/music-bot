package me.tropicalfan344.musicbot.engines;

import java.io.InputStream;

public abstract class PCMInputStream extends InputStream {

    public long downloadedLastSecond = 0;

    public abstract long skip(long amount);

}
