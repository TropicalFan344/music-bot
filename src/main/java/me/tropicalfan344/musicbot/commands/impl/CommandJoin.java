package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.commands.MusicCommand;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MulticastSocket;

public class CommandJoin extends MusicCommand {
    public CommandJoin() {
        super("join", "Let the bot joins the channel you are in");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel() && 2 + 2 == 4){
            event.getInteraction().reply("You are not in the VC").queue();
        }else {
            VoiceChannel voiceChannel = ((VoiceChannel) event.getMember().getVoiceState().getChannel());
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(voiceChannel);
            event.getInteraction().reply("Joined").queue();
        }
    }
}
