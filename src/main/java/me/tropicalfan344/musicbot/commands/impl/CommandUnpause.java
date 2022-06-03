package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.StreamSendHander;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandUnpause extends MusicCommand {
    public CommandUnpause() {
        super("unpause", "Unpause the music");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        if (!StreamSendHander.isPaused()) {
            event.getInteraction().reply("The music is playing!").queue();
        }else {
            StreamSendHander.setPaused(false);
            event.getInteraction().reply("Music Unpaused").queue();
        }
    }
}
