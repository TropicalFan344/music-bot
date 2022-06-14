package me.tropicalfan344.musicbot.commands;

import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.MusicBot;
import me.tropicalfan344.musicbot.commands.impl.CommandEffectsAdd;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.utils.SimpleEmbedGenerator;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandsManager {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(69);
    private final List<MusicCommand> commands = new ArrayList<>();

    public <E extends MusicCommand> E getCommandByClass(Class<E> clazz) {
        for (MusicCommand command : commands) {
            if (command.getClass() == clazz) {
                return (E) command;
            }
        }
        return null;
    }

    @SneakyThrows
    public CommandsManager(MusicBot musicBot) {

        for (Class<? extends MusicCommand> commandClazz : musicBot.getReflections().getSubTypesOf(MusicCommand.class)) {
            try {
                MusicCommand command = commandClazz.newInstance();
                command.musicBot = musicBot;
                commands.add(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Class<? extends AudioEffect> aClass : AudioEffectsManager.registry) {
            commands.add(new CommandEffectsAdd(aClass.newInstance()));
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
                        threadPool.submit(() -> {
                            try {
                                System.out.println("[CommandsManager] [" + Thread.currentThread().getName() + "] Executing Command: " + event.getName());
                                command.onExecute(event);
                                System.out.println("[CommandsManager] [" + Thread.currentThread().getName() + "] Command Executed");
                            } catch (Throwable throwable) {
                                if (throwable instanceof CommandException) {
                                    try {

                                        event.getHook().editOriginal(new MessageBuilder(
                                                SimpleEmbedGenerator.generateErrorEmbed(throwable.getMessage())
                                        ).build()).queue();

                                        event.getInteraction().reply(new MessageBuilder(
                                                SimpleEmbedGenerator.generateErrorEmbed(throwable.getMessage())
                                        ).build()).queue();
                                    } catch (Throwable e) {

                                    }
                                } else {
                                    throwable.printStackTrace();
                                    try {

                                        event.getHook().editOriginal(new MessageBuilder(
                                                // TODO: Error report system
                                                SimpleEmbedGenerator.generateErrorEmbed("Something went wrong while handling the command! Error: " + throwable.getMessage())
                                        ).build()).queue();

                                        event.getInteraction().reply(new MessageBuilder(
                                                // TODO: Error report system
                                                SimpleEmbedGenerator.generateErrorEmbed("Something went wrong while handling the command! Error: " + throwable.getMessage())
                                        ).build()).queue();
                                    } catch (Throwable e) {

                                    }
                                }
                            }
                        });
                        return;
                    }
                }
            }
        });
    }



}
