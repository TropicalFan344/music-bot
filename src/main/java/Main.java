import lombok.SneakyThrows;
import me.tropicalfan344.musicbot.commands.CommandsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Main {
    @SneakyThrows
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault("OTgxOTY5ODI2MDMxNDk3Mjk2.G0Xz5u.1y_KaD8o2TtYA3U7cJcu2-dALv4DLvN4AYD8HE").setActivity(Activity.playing("Your Mom")).build();


        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(@NotNull MessageReceivedEvent event) {
                super.onMessageReceived(event);
                if (event.getMessage().getContentRaw().equals("__delete_command__")) {
                    event.getGuild().deleteCommandById("979725835961503754");
                    event.getGuild().deleteCommandById("981967991967199283");
                    event.getGuild().deleteCommandById("979725836800372797");
                    event.getGuild().deleteCommandById("979774104641085460");
                    event.getGuild().deleteCommandById("981876211582119956");
                    event.getGuildChannel().sendMessage("Commands deleted!").queue();
                }
            }
        });
        System.out.println("Listeners loaded");

        System.out.println("Commands loaded");

        jda.awaitReady();
        CommandsManager commandsManager = new CommandsManager(jda);

    }

}
