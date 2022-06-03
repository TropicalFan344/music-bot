package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue extends MusicCommand {

    public static List<Track> queue = new ArrayList<>();

    public CommandQueue() {
        super("queue", "Show queue", new OptionData(OptionType.STRING, "query", "queue songs"));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (event.getOptions().size() == 1) {
            YouTubeEngine engine = new YouTubeEngine();
            ISearchResult result = engine.search(event.getOption("query").getAsString());
            List<Track> results = result.getResults();
            for (Track track : results) {
                System.out.println("Found: " + track.getTitle());
            }
            Track targetTrack = results.get(0);
            event.getInteraction().reply("> **Queued: ** " + targetTrack.getTitle() + " (" + targetTrack.getUrl() + ")").queue();
            queue.add(targetTrack);
        }else {
            if (queue.size() == 0) {
                event.getInteraction().reply("There is nothing in queue").queue();
            }else {
                String list = "";
                int i = 1;
                for (Track track : queue) {
                    if (i == 1) {
                        list = list + i + ". " + track.getTitle() + "  <-  **Now Playing**" + "\n";
                    }else {
                        list = list + i + ". " + track.getTitle() + "\n";
                    }
                    i++;
                }
                event.getInteraction().reply(list).queue();
            }
        }
    }
}
