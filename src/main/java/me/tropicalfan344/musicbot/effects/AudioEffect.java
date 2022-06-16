package me.tropicalfan344.musicbot.effects;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.effects.options.EffectOption;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.swing.text.html.Option;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class AudioEffect {


    @Getter
    private final String id;
    @Getter
    private final String displayName;


    public AudioEffect(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public abstract void processAudio(float[] audioData);

    @Override
    public String toString() {
        return displayName;
    }

    @SneakyThrows
    public List<EffectOption<?>> getAllOptions() {
        List<EffectOption<?>> options = new ArrayList<>();
        for (Field field : getClass().getFields()) {
            if (EffectOption.class.isAssignableFrom(field.getType())) {
                options.add((EffectOption<?>) field.get(this));
            }
        }
        return options;
    }

    @SneakyThrows
    public List<OptionData> getAllCommandsOptions() {
        List<OptionData> options = new ArrayList<>();

        for (EffectOption<?> option : getAllOptions()) {
            options.add(new OptionData(option.getArgumentOptionType(), option.getName(), option.getDescription(), option.isRequired()));
        }

        return options;
    }

}
