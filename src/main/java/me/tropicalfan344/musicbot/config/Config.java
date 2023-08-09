package me.tropicalfan344.musicbot.config;

import me.tropicalfan344.musicbot.connection.MusicBotConstants;

public class Config {

    public String token = System.getenv("DISCORD_TOKEN");
    public int communicationPort = MusicBotConstants.defaultPort;

}
