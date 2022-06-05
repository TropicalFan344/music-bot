package me.tropicalfan344.musicbot.effects.options.impl;

import lombok.Getter;
import me.tropicalfan344.musicbot.effects.options.EffectOption;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class FloatOption extends EffectOption<Float> {

    @Getter
    private final float minValue;
    @Getter
    private final float maxValue;

    public FloatOption(String name, String description, boolean required, float defaultValue, float minValue, float maxValue) {
        super(name, description, required, defaultValue, OptionType.NUMBER);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    protected boolean isValid(Float value) {
        return value >= minValue && value <= maxValue;
    }

    @Override
    protected Float extractValue(CommandInteraction interaction) {
        return (float) interaction.getOption(getName()).getAsDouble();
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Range: " + minValue + " - " + maxValue;
    }
}
