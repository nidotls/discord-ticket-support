package io.nilsdev.ticketsupport.bot.tasks;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

public class PresenceUpdateTask implements Runnable {

    private final JDA jda;

    @Inject
    public PresenceUpdateTask(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        Integer memberCount = this.jda.getGuilds()
                .stream()
                .map(Guild::getMemberCount)
                .reduce(0, Integer::sum);

        String activity = ".ticket | " + this.jda.getGuilds().size() + " Server | " + memberCount + " Member";

        this.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(activity));
    }
}
