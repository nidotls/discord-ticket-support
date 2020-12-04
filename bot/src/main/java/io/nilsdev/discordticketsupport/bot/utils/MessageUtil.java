package io.nilsdev.discordticketsupport.bot.utils;

import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.logging.log4j.Logger;

public class MessageUtil {

    public static void disposableMessage(Logger logger, TextChannel channel, String text) {
        channel.sendMessage(text).queue(message -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            message.delete().queue();
        }, logger::throwing);
    }
}
