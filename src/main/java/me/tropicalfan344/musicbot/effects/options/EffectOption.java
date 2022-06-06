package me.tropicalfan344.musicbot.effects.options;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class EffectOption<T> {

    @Getter
    private T value;

    @Getter
    private final T defaultValue;

    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final boolean required;

    @Getter
    private final OptionType argumentOptionType;

    public EffectOption(String name, String description, boolean required, T defaultValue, OptionType argumentOptionType) {
        this.name = name;
        this.description = description + ", Default: " + getDefaultValue();
        this.required = required;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.argumentOptionType = argumentOptionType;
    }

    public OptionData generateArgOption() {
        return new OptionData(getArgumentOptionType(), getName(), getDescription(), isRequired());
    }

    public boolean setValue(T value) {
        if (isValid(value)) {
            this.value = value;
            return true;
        }
        return false;
    }

    protected boolean isValid(T value) {
        return true;
    }

    public abstract T extractValue(CommandInteraction interaction);


    public MessageEmbed.Field getOptionDisplay() {
        return new MessageEmbed.Field(getName(), getDescription(), false);
    }

}
