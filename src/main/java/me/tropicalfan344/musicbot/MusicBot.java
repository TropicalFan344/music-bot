package me.tropicalfan344.musicbot;

import lombok.Getter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.commands.CommandsManager;
import me.tropicalfan344.musicbot.config.Config;
import me.tropicalfan344.musicbot.config.ConfigManager;
import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.effects.AudioEffectsManager;
import me.tropicalfan344.musicbot.engines.Engine;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.reflections.Reflections;

public class MusicBot {

    public static MusicBot instance;

    @Getter private final Engine engine = new YouTubeEngine();

    @Getter private final JDA jda;
    @Getter private final Reflections reflections;
    @Getter private final CommandsManager commandsManager;
    @Getter private final ConfigManager configManager;


    @SneakyThrows
    public MusicBot() {
        instance = this;
        this.reflections = new Reflections(getClass().getPackage().getName());
        this.configManager = new ConfigManager();
        this.jda = JDABuilder
                .createDefault(configManager.getConfig().token)
                .setActivity(Activity.playing("Gayshin Impact"))
                .build();

        System.out.println("Preparing Bot...");
        jda.awaitReady();
        System.out.println("JDA has connected to Discord as user: " + jda.getSelfUser());

        this.commandsManager = new CommandsManager(this);
    }

}
