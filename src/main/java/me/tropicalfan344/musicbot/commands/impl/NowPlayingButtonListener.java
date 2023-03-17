package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.CommandsManager;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NowPlayingButtonListener extends ListenerAdapter {

    private CommandNowPlaying command;

    public NowPlayingButtonListener(CommandNowPlaying command) {
        this.command = command;
    }

    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(command.musicBot, event.getGuild());
        if (event.getButton().getId().equals("status")) {
            if (musicManager.isPaused()) {
                musicManager.resume();
                reply(musicManager, event, "\n▶️ Resumed");
            }else {
                musicManager.pause();
                reply(musicManager, event, "\n⏸ Paused");
            }
        }else if (event.getButton().getId().equals("loopMode")) {
            switch (musicManager.getLoopMode().getName()) {
                case "Off":
                    musicManager.setLoopMode(GuildMusicManager.LoopMode.SINGLE);
                    break;
                case "Single":
                    musicManager.setLoopMode(GuildMusicManager.LoopMode.ALL);
                    break;
                case "All":
                    musicManager.setLoopMode(GuildMusicManager.LoopMode.OFF);
                    break;
            }
            reply(musicManager, event, "\nChanged loop mode to : " + musicManager.getLoopMode().getEmoji() + musicManager.getLoopMode().getName());
        }else if (event.getButton().getId().equals("skip")) {
            musicManager.skip();
            reply(musicManager, event, "\n►► Skipped");
        }else if (event.getButton().getId().equals("shuffle")) {
            musicManager.shuffle();
            reply(musicManager, event, "\n\uD83D\uDD00 Shuffled");
        }else if (event.getButton().getId().equals("refresh")) {
            reply(musicManager, event, "");
        }
    }

    private void reply(GuildMusicManager musicManager, ButtonInteractionEvent event, String extraDes) {
        if (musicManager.getSendHandler() == null) {
            throw new CommandException("The bot is not playing any music at the moment");
        }else {
            int totalLength = musicManager.getQueue().get(0).getLength();
            //unit:sec
            int currentTime = (int) musicManager.getSendHandler().getTime() / 1000;
            //unit:ms
            int currentMinute = currentTime / 60;
            int currentSecond = currentTime % 60;
            int totalMinute = totalLength / 60;
            int totalSecond = totalLength % 60;
            String time = String.format("%02d:%02d / %02d:%02d", currentMinute, currentSecond, totalMinute, totalSecond);
            float progress = currentTime*1.0f/totalLength;
            String description = "";
            int before = (int) (progress * 20);
            int after = 20 - before;
            description += repeat("─", before) + "◉" + repeat("─", after);
            description += "\n" + musicManager.getLoopMode().getEmoji() + "   ◄◄  " + (musicManager.getSendHandler().isPaused()?":arrow_forward:":":pause_button:") + "  ►►  " + time;
            description += extraDes;

            Track targetTrack = musicManager.getQueue().get(0);
            MessageEmbed builder = new EmbedBuilder()
                    .setTitle(targetTrack.getTitle(), targetTrack.getUrl())
                    .setAuthor("ɴᴏᴡ ᴘʟᴀʏɪɴɢ:")
                    .setDescription(description)
                    .setThumbnail(targetTrack.getThumbnail())
                    .setColor(SimpleEmbedGenerator.SUCCESS)
                    .build();
            MessageEditCallbackAction reply = event.editMessageEmbeds(builder);
            List<Button> buttons = new ArrayList<>();
            if (musicManager.isPaused()) {
                buttons.add(Button.primary("status", "▶️"));
            }else {
                buttons.add(Button.primary("status", "⏸"));
            }
            buttons.add(Button.primary("skip", "►►"));
            buttons.add(Button.primary("loopMode", musicManager.getLoopMode().getEmoji()));
            buttons.add(Button.primary("shuffle", "\uD83D\uDD00"));
            buttons.add(Button.primary("refresh", "\uD83D\uDDD8"));
            reply.setActionRow(buttons);
            reply.queue();
        }
    }

    private static String repeat(String input, int amount) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            output.append(input);
        }
        return output.toString();
    }

}
