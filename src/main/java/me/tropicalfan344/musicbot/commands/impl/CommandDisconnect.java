package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandDisconnect extends MusicCommand {
    public CommandDisconnect() {
        super("disconnect", "Disconnect from the channel that the bot is in");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildVoiceState selfVoiceState = event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState();
        if (!selfVoiceState.inAudioChannel()){
            throw new CommandException("I'm not in a voice channel right now! Use /join to let me join one.");

        }
        if (!selfVoiceState.getChannel().getMembers().contains(event.getMember())) {
            throw new CommandException("You're not in the same voice channel as me.");
        }

        GuildMusicManager.getMusicManager(musicBot, event.getGuild()).disconnectFromVoiceChannel();
        AudioChannel channel = event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState().getChannel();
        event.getInteraction().replyEmbeds(
                SimpleEmbedGenerator.generateSuccessfulEmbed("Successfully disconnected from " + channel.getAsMention() + ", AutoPlay is now **Disabled**")
        ).queue();

    }
}