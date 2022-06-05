package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue extends MusicCommand {


    public CommandQueue() {
        super("queue", "Show all songs in the queue.");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager manager = GuildMusicManager.getMusicManager(event.getGuild());
        if (manager.getQueue().size() == 0) {
            event.getInteraction().reply("There is nothing in queue.").queue();
        }else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Songs in Queue");
            String out = "";
            int i = 1;
            for (Track track : manager.getQueue()) {
                if (i == 1) {
                    out = out + i + ". " + track.getTitle() + "  <-  **Now Playing**" + "\n";
                }else {
                    out = out + i + ". " + track.getTitle() + "\n";
                }
                i++;
            }
            event.getInteraction().reply(new MessageBuilder(builder).build()).queue();
        }
    }
}
