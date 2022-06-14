package me.tropicalfan344.musicbot.effects.impl;

import me.tropicalfan344.musicbot.effects.AudioEffect;
import me.tropicalfan344.musicbot.effects.options.impl.FloatOption;
import org.jetbrains.annotations.Range;
import uk.me.berndporr.iirj.Butterworth;

public class EffectVolume extends AudioEffect {

    /**
     * Un-normalized
     */
    public final FloatOption volume = new FloatOption("volume", "Volume. The value is un-normalized.", true, 100f, 1f, Float.POSITIVE_INFINITY);

    public EffectVolume() {
        super("volume", "Volume");
    }

    @Override
    public void processAudio(float[] audioData) {
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = audioData[i] * (volume.getValue() / 100f);
        }

    }

    @Override
    public String toString() {
        return super.toString() + " (" + volume.getValue() + "%)";
    }
}
