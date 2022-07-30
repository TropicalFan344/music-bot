package me.tropicalfan344.musicbot;

import lombok.Getter;
import lombok.SneakyThrows;
import me.fan87.facket.api.FacketServer;
import me.tropicalfan344.musicbot.connection.ConnectionManager;
import me.tropicalfan344.musicbot.connection.MusicBotConnection;
import me.tropicalfan344.musicbot.connection.MusicBotConstants;
import me.tropicalfan344.musicbot.commands.CommandsManager;
import me.tropicalfan344.musicbot.config.ConfigManager;
import me.tropicalfan344.musicbot.engines.Engine;
import me.tropicalfan344.musicbot.engines.impl.youtube.YouTubeEngine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.reflections.Reflections;

import java.net.InetSocketAddress;

public class MusicBot {

    public static MusicBot instance;

    @Getter private final Engine engine = new YouTubeEngine();

    @Getter private final JDA jda;
    @Getter private final Reflections reflections;
    @Getter private final CommandsManager commandsManager;
    @Getter private final ConfigManager configManager;
    @Getter private final ConnectionManager connectionManager;



    @SneakyThrows
    public MusicBot() {
        instance = this;
        this.reflections = new Reflections(getClass().getPackage().getName());
        this.configManager = new ConfigManager();
        this.jda = JDABuilder
                .createDefault(configManager.getConfig().token)
                .setActivity(Activity.playing("TOWA"))
                .build();


        System.out.println("Preparing Bot...");
        jda.awaitReady();
        System.out.println("JDA has connected to Discord as user: " + jda.getSelfUser());

        this.commandsManager = new CommandsManager(this);
        this.connectionManager = new ConnectionManager(this);
    }

}
