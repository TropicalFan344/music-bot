package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.TrackSendHandler;
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
        event.getInteraction().deferReply().queue();
        GuildMusicManager musicManager = GuildMusicManager.getMusicManager(musicBot, event.getGuild());
        int totalLength = musicManager.getQueue().get(0).getLength();
        //unit:sec
        int currentTime = ((int) (((TrackSendHandler) musicManager.getGuildAudioManager().getSendingHandler()).getTime() / 1000));
        //unit:ms
        int currentMinute = currentTime / 60;
        int currentSecond = currentTime % 60;
        int totalMinute = totalLength / 60;
        int totalSecond = totalLength % 60;
        String time = String.format("%02d:%02d / %02d:%02d", currentMinute, currentSecond, totalMinute, totalSecond);
        float progress = currentTime*1.0f/totalLength;
        String totalStringDisplayProgress = "";
        for (int i = 1; i < 21; i++) {
            if (i == ((int) (progress * 20))) {
                totalStringDisplayProgress = totalStringDisplayProgress + "◉";
            }
            totalStringDisplayProgress = totalStringDisplayProgress + "─";
        }

        Track targetTrack = musicManager.getQueue().get(0);
        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setTitle(targetTrack.getEmbedDisplay())
                .setAuthor("ɴᴏᴡ ᴘʟᴀʏɪɴɢ:")
                .setDescription(totalStringDisplayProgress + "  " + time)
                .setThumbnail(targetTrack.getThumbnail())
                .setColor(SimpleEmbedGenerator.SUCCESS)
                .build()).queue();
    }
}
