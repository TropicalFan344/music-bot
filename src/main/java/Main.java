import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.MusicBot;
import me.tropicalfan344.musicbot.commands.CommandsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Main {
    @SneakyThrows
    public static void main(String[] args) throws LoginException {
        MusicBot bot = new MusicBot();
    }

}
