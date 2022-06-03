package me.tropicalfan344.musicbot.commands.impl;

import com.google.gson.internal.Streams;
import me.tropicalfan344.musicbot.StreamSendHander;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

public class CommandLeave extends MusicCommand {
    public CommandLeave() {
        super("leave", "Disconnect from the channel that the bot is in");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (!event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState().inAudioChannel()){
            event.getInteraction().reply("I'm not in a voice channel right now! Use /join to let me join one.").queue();
        }else {
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.closeAudioConnection();
            StreamSendHander.setPaused(true);
            event.getInteraction().reply("Successfully disconnected from <#" + event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState().getChannel().getId() + ">").queue();
        }
    }
}