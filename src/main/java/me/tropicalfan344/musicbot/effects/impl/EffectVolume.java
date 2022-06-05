package me.tropicalfan344.musicbot.effects.impl;

import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.effects.options.impl.FloatOption;
import org.jetbrains.annotations.Range;

public class EffectVolume extends AudioEffect {

    /**
     * Un-normalized
     */
    private final FloatOption volume = new FloatOption("volume", "Volume. The value is un-normalized.", true, 100f, 1f, Float.POSITIVE_INFINITY);

    public EffectVolume() {
        super("volume", "Volume");
    }

    @Override
    public void processAudio(float[] audioData) {
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = audioData[i] * volume.getValue();
        }
    }

}
