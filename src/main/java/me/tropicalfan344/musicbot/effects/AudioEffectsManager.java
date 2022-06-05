package me.tropicalfan344.musicbot.effects;

import lombok.Getter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.MusicBot;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AudioEffectsManager {

    @Getter
    private final List<AudioEffect> effects = new ArrayList<>();

    @SneakyThrows
    public AudioEffectsManager(MusicBot bot) {
        Reflections reflections = bot.getReflections();
        Set<Class<? extends AudioEffect>> subTypesOf = reflections.getSubTypesOf(AudioEffect.class);
        for (Class<? extends AudioEffect> aClass : subTypesOf) {
            effects.add(aClass.getConstructor().newInstance());
        }
    }


}
