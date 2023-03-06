package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.TrackSendHandler;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.List;

public class CommandNowPlaying extends MusicCommand {
    public CommandNowPlaying() {
        super("nowplaying", "Show the song that's now being played.");
    }

    public void init() {
        musicBot.getJda().addEventListener(new NowPlayingButtonListener(this));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (musicManager.getQueue().size() == 0) {
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

            Track targetTrack = musicManager.getQueue().get(0);
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(targetTrack.getTitle(), targetTrack.getUrl())
                    .setAuthor("ɴᴏᴡ ᴘʟᴀʏɪɴɢ:")
                    .setDescription(description)
                    .setThumbnail(targetTrack.getThumbnail())
                    .setColor(SimpleEmbedGenerator.SUCCESS)
                    .build();
            ReplyCallbackAction reply = event.getInteraction().replyEmbeds(embed);
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
            reply.addActionRow(buttons);
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

