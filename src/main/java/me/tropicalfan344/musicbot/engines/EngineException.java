package me.tropicalfan344.musicbot.engines;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class EngineException extends RuntimeException {

    private final String userMessage;
    private final String detailedLoggedMessage;

    public EngineException(String userMessage, String detailedLoggedMessage) {
        super(userMessage);
        this.userMessage = userMessage;
        this.detailedLoggedMessage = detailedLoggedMessage;
    }

    public EngineException(String userMessage, String detailedLoggedMessage, Throwable throwable) {
        super(userMessage, throwable);
        this.userMessage = userMessage;
        this.detailedLoggedMessage = detailedLoggedMessage;
    }

}
