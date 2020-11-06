package io.nilsdev.ticketsupport.bot.tasks;

import com.google.inject.Inject;
import io.nilsdev.ticketsupport.bot.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PresenceUpdateTask implements Runnable {

    private final JDA jda;

    @Inject
    public PresenceUpdateTask(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        String version = "Unknown";

        try (InputStream inputStream = Bot.class.getClassLoader().getResourceAsStream("git.properties")) {
            Properties properties = new Properties();

            properties.load(inputStream);

            version = properties.getProperty("git.commit.id.describe");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String activity = ".ticket help | Version " + version;

        this.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(activity));
    }
}
