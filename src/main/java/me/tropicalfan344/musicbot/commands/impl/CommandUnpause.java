package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.StreamSendHandler;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandUnpause extends MusicCommand {
    public CommandUnpause() {
        super("unpause", "Unpause the music");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (!StreamSendHandler.isPaused()) {
            event.getInteraction().reply("The music is playing!").queue();
        }else {
            StreamSendHandler.setPaused(false);
            event.getInteraction().reply("Music Unpaused").queue();
        }
    }
}
