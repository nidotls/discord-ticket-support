package io.nilsdev.discordticketsupport.bot.tasks;

import com.google.inject.Inject;
import io.nilsdev.discordticketsupport.bot.utils.VersionUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class PresenceUpdateTask implements Runnable {

    private final JDA jda;

    @Inject
    public PresenceUpdateTask(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        String activity = ".ticket help | Version " + VersionUtil.getVersion();

        this.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(activity));
    }
}
