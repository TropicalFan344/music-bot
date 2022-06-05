package me.tropicalfan344.musicbot.commands;

import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.MusicBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class CommandsManager {

    private final List<MusicCommand> commands = new ArrayList<>();

    @SneakyThrows
    public CommandsManager(MusicBot musicBot) {

        for (Class<? extends MusicCommand> commandClazz : musicBot.getReflections().getSubTypesOf(MusicCommand.class)) {
            MusicCommand command = commandClazz.newInstance();
            command.musicBot = musicBot;
            if (1 + 1 == 2) {
                commands.add(command);
            }
        }

        CommandListUpdateAction action = musicBot.getJda().getGuildById(979552134850834492L).updateCommands();
        for (MusicCommand command : commands) {
            action.addCommands(command.getCommandData());
        }
        action.queue();

        // Register Listeners
        musicBot.getJda().addEventListener(new ListenerAdapter() {
            @Override
            public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
                for (MusicCommand command : commands) {
                    if (command.getName().equals(event.getName())) {
                        try {
                            command.onExecute(event);
                        } catch (Throwable throwable) {
                            event.getInteraction().reply("Something went wrong while handling the command! Error: " + throwable.getMessage());
                        }
                        return;
                    }
                }
            }
        });
    }



}
