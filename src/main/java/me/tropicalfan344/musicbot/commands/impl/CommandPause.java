package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.StreamSendHander;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.stream.Stream;

public class CommandPause extends MusicCommand {
    public CommandPause() {
        super("pause", "Pause the music");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (StreamSendHander.isPaused()) {
            event.getInteraction().reply("The music is already stopped!").queue();
        }else {
            StreamSendHander.setPaused(true);
            event.getInteraction().reply("Music paused!").queue();
        }
    }
}
