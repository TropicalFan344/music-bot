package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.io.IOException;

public class CommandSave extends MusicCommand {
    public CommandSave() {
        super("save", "Save all songs in queue", new OptionData(OptionType.STRING, "name", "name of list", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) throws IOException {
        File list = new File("saves/" + event.getGuild().getId());
        if (!list.exists()) {
            list.createNewFile();
        }
    }
}
