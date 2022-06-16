package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.TrackSendHandler;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandNowPlaying extends MusicCommand {
    public CommandNowPlaying() {
        super("nowplaying", "Show the song that's now being played.");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        if (musicManager.getSendHandler() == null) {
            throw new CommandException("The bot is not playing any music at the moment");
        }
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
        int before = (int) progress * 20;
        int after = 20 - before;
        description += repeat("─", before) + "◉" + repeat("─", after);

        description += "\n◄◄⠀" + (musicManager.getSendHandler().isPaused()?":pause_button:":":arrow_forward:") + "\"⠀►►   " + time;

        Track targetTrack = musicManager.getQueue().get(0);
        event.getHook().replyEmbeds(new EmbedBuilder()
                .setTitle(targetTrack.getTitle(), targetTrack.getUrl())
                .setAuthor("ɴᴏᴡ ᴘʟᴀʏɪɴɢ:")
                .setDescription(description)
                .setThumbnail(targetTrack.getThumbnail())
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
    }


    private static String repeat(String input, int amount) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            output.append(input);
        }
        return output.toString();
    }

}

