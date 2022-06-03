package me.tropicalfan344.musicbot.engines;

import java.util.List;
import java.util.function.Supplier;

public interface Engine {

    ISearchResult search(String query);
    boolean canProvide(String url);
    Track provide(String url);

}
