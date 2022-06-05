package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.StreamSendHandler;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandPause extends MusicCommand {
    public CommandPause() {
        super("pause", "Pause the music");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (StreamSendHandler.isPaused()) {
            event.getInteraction().reply("The music is already stopped!").queue();
        }else {
            StreamSendHandler.setPaused(true);
            event.getInteraction().reply("Music paused!").queue();
        }
    }
}
