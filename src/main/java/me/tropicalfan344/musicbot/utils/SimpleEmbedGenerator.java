package me.tropicalfan344.musicbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class SimpleEmbedGenerator {

    private static MessageEmbed generateMessageEmbed(Color color, String message) {
        return new EmbedBuilder()
                .setDescription(message)
                .setColor(color)
                .build();
    }

    public static MessageEmbed generateErrorEmbed(String message) {
        return generateMessageEmbed(new Color(0xff4747), message);
    }

    public static MessageEmbed generateSuccessfulEmbed(String message) {
        return generateMessageEmbed(new Color(0x5DFF51), message);
    }

    public static MessageEmbed generateWarningEmbed(String message) {
        return generateMessageEmbed(new Color(0xFAFF5B), message);
    }

    public static Color WARNING = new Color(0xFAFF5B);
    public static Color ERROR = new Color(0xff4747);
    public static Color SUCCESS = new Color(0x5DFF51);


}
