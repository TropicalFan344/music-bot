package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.StreamSendHandler;
import me.tropicalfan344.musicbot.commands.MusicCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandVolume extends MusicCommand {
    public CommandVolume() {
        super("volume", "Set the volume of your music. Don't set the volume to high, if you want to broke your ears...", new OptionData(OptionType.INTEGER, "value", "Volume of your music", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        StreamSendHandler.volume = event.getOption("value").getAsInt();
        event.getInteraction().reply("Successfully set the volume to: " + event.getOption("value").getAsInt() + "‰").queue();
    }
}
