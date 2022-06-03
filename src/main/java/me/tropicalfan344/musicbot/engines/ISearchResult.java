package me.tropicalfan344.musicbot.engines;

import java.util.List;

public interface ISearchResult {

    List<Track> getResults();
    ISearchResult nextPage();

}
