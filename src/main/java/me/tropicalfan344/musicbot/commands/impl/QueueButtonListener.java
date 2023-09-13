package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QueueButtonListener extends ListenerAdapter {
    private final CommandQueue commandQueue;

    public QueueButtonListener(CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().matches("goPage:\\d+")) {
            String[] split = event.getButton().getId().split(":");
            int page = Integer.parseInt(split[1]);
            GuildMusicManager manager = GuildMusicManager.getMusicManager(commandQueue.musicBot, event.getGuild());
            if (manager.getQueue().size() == 0) {
                event.reply("There is nothing in queue.").queue();
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
                    if (i == manager.getIndex()) {
                        out += "  <-  **Now Playing**" + "\n";
                    }
                    out += "\n";
                }
                builder.setDescription(out);
                builder.setColor(SimpleEmbedGenerator.SUCCESS);
                MessageEditCallbackAction reply = event.editMessageEmbeds(builder.build());
                List<Button> buttons = new ArrayList<>();

                if (requestedPage > 1) {
                    buttons.add(Button.primary("goPage:" + (requestedPage - 1), "Previous Page"));
                }
                if (requestedPage < totalPages) {
                    buttons.add(Button.primary("goPage:" + (requestedPage + 1), "Next Page"));
                }
                reply.setActionRow(buttons.toArray(new Button[0]));
                reply.queue();
            }
        }
    }
}
