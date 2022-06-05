package me.tropicalfan344.musicbot.effects;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class AudioEffect {

    @Getter
    @Setter
    private boolean enabled = false;

    @Getter
    private final String id;
    @Getter
    private final String displayName;


    public AudioEffect(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    protected abstract void processAudio(float[] audioData);

    public void process(float[] audioData) {
        if (enabled) {
            processAudio(audioData);
        }
    }

    @Override
    public String toString() {
        return displayName;
    }

}
