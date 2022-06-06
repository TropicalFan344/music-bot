package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.GuildMusicManager;
import me.tropicalfan344.musicbot.commands.CommandException;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class CommandJoin extends MusicCommand {
    public CommandJoin() {
        super("join", "Let the bot joins the channel you are in");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        GuildVoiceState selfVoiceState = event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getVoiceState();
        if (!event.getMember().getVoiceState().inAudioChannel()){
            throw new CommandException("You are not in a voice channel right now!");
        }
        if (selfVoiceState.inAudioChannel()) {
            throw new CommandException("The bot is already in a voice channel! Please use /leave before letting it join another.");
        }

        VoiceChannel voiceChannel = ((VoiceChannel) event.getMember().getVoiceState().getChannel());
        GuildMusicManager.getMusicManager(musicBot, event.getGuild()).joinVoiceChannel(voiceChannel);
        event.getInteraction().reply(new MessageBuilder(SimpleEmbedGenerator.generateSuccessfulEmbed(
                "Successfully joined " + voiceChannel.getAsMention()
        )).build()).queue();

    }
}
