package me.tropicalfan344.musicbot.commands;

import lombok.SneakyThrows;
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

    public CommandsManager(JDA jda) {

        Reflections reflections = new Reflections("me");
        for (Class<? extends MusicCommand> commandClazz : reflections.getSubTypesOf(MusicCommand.class)) {
            MusicCommand command = commandClazz.newInstance();
            if (1 + 1 == 2) {
                commands.add(command);
                System.out.println("1 + 1 == 2 & registered command: " + command.getName());
            }
        }

        CommandListUpdateAction action = jda.getGuildById(979552134850834492L).updateCommands();
        for (MusicCommand command : commands) {
            action.addCommands(command.getCommandData());
            System.out.println("registing " + command.getName());
        }
        action.queue();

        // Register Listeners
        jda.addEventListener(new ListenerAdapter() {
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
