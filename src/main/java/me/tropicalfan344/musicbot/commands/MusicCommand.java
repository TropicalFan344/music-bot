package me.tropicalfan344.musicbot.commands;

import lombok.Getter;
import me.tropicalfan344.musicbot.MusicBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.io.IOException;

public abstract class MusicCommand {

    public CommandData getCommandData() {
        return new CommandDataImpl(name, description).addOptions(options);
    }


    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final OptionData[] options;

    public MusicBot musicBot;

    public MusicCommand(String name, String description, OptionData... options) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public void init() {

    }

    public abstract void onExecute(SlashCommandInteractionEvent event) throws IOException;

}
