package me.tropicalfan344.musicbot.commands.impl;

import me.tropicalfan344.musicbot.commands.MusicCommand;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandLinkClient extends MusicCommand {


    public CommandLinkClient() {
        super("link-client", "Link to a client");
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        String key = musicBot.getConnectionManager().getKeyOf(event.getUser());
        event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Your link key: `" + key + "`. Do NOT share it to anyone!")).queue();
        event.getInteraction().replyEmbeds(SimpleEmbedGenerator.generateSuccessfulEmbed("Please check your DM for link key")).queue();
    }
}
