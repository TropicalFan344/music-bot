package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue extends MusicCommand {


    public CommandQueue() {
        super("queue", "Show all songs in the queue.", new OptionData(OptionType.INTEGER, "page", "The page number", false));
    }

    @Override
    public void init() {
        musicBot.getJda().addEventListener(new QueueButtonListener(this));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        OptionMapping page = event.getOption("page");
        reply(page != null?page.getAsInt():1, event.getInteraction(), event.getGuild());
    }

    public void reply(int page, IReplyCallback interaction, Guild guild) {
        GuildMusicManager manager = GuildMusicManager.getMusicManager(musicBot, guild);
        if (manager.getQueue().size() == 0) {
            interaction.replyEmbeds(SimpleEmbedGenerator.generateErrorEmbed("There is nothing in queue.")).queue();
        }else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Songs in Queue");
            String out = "";
            int requestedPage = page;
            int pageAmount = 15;
            int totalPages = (int) Math.ceil(manager.getQueue().size() * 1.0 / pageAmount);
            requestedPage = Math.min(Math.max(requestedPage, 1), totalPages);
            int startIndex = (requestedPage - 1) * pageAmount; // Inclusive
            int endIndex = Math.min(manager.getQueue().size(), requestedPage * pageAmount); // Exclusive
            for (int i = startIndex; i < endIndex; i++) {
                Track track = manager.getQueue().get(i);
                out += (i + 1) + ". " + track.getEmbedDisplay();
                if (i == 0) {
                    out += "  <-  **Now Playing**" + "\n";
                }
                out += "\n";
            }
            builder.setDescription(out);
            builder.setColor(SimpleEmbedGenerator.SUCCESS);
            ReplyCallbackAction reply = interaction.reply(new MessageBuilder(builder).build());
            if (requestedPage > 1) {
                reply.addActionRow(Button.primary("goPage:" + (requestedPage - 1), "Previous Page"));
            }
            if (requestedPage < totalPages) {
                reply.addActionRow(Button.primary("goPage:" + (requestedPage + 1), "Next Page"));
            }
            reply.queue();
        }
    }
}
