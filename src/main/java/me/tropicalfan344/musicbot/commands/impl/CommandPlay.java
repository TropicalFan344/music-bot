package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.StreamSendHandler;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.ISearchResult;
import me.tropicalfan344.musicbot.engines.Track;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class CommandPlay extends MusicCommand {
    public CommandPlay() {
        super("play", "yeah...you know", new OptionData(OptionType.STRING, "query", "Use url or just search", true));
    }

    public static boolean played;

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (!event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState().inAudioChannel()) {
            event.getInteraction().reply("I am not in the voice channel, use /join to let me join your voice channel.").queue();
        }else {
            played = true;
            List<Track> queue = CommandQueue.queue;
            event.getInteraction().deferReply();
            AudioManager manager = event.getGuild().getAudioManager();
            YouTubeEngine engine = new YouTubeEngine();
            ISearchResult result = engine.search(event.getOption("query").getAsString());
            List<Track> results = result.getResults();
            final Track[] targetTrack = {results.get(0)};
            event.getInteraction().reply("> **Now Playing: **: " + targetTrack[0].getTitle() + " (" + targetTrack[0].getUrl() + ")").queue();
            StreamSendHandler handler = new StreamSendHandler(targetTrack[0].getPCMStream(), GuildMusicManager.getMusicManager(event.getGuild()));
            manager.setSendingHandler(handler);
            StreamSendHandler.setDone(false);
            StreamSendHandler.setPaused(false);
            queue.add(targetTrack[0]);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (StreamSendHandler.isDone() && queue.size() >= 2) {
                            queue.remove(0);
                            targetTrack[0] = queue.get(0);
                            StreamSendHandler.setDone(false);
                            StreamSendHandler.setPaused(false);
                            event.getChannel().sendMessage("> **Now Playing: **: " + targetTrack[0].getTitle() + " (" + targetTrack[0].getUrl() + ")").queue();
                            manager.setSendingHandler(new StreamSendHandler(targetTrack[0].getPCMStream()));
                            played = true;
                        }
                        if (StreamSendHandler.isDone() && queue.size() == 1){
                            queue.remove(0);
                            break;
                        }
                    }
                }
            }).start();
        }
    }
}
