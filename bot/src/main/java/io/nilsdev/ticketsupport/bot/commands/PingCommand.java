package io.nilsdev.ticketsupport.bot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

@CommandController
public class PingCommand {

    @Command("ping")
    public void onCommand(CommandEvent event) {
        Message message = event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", Ping...").complete();

        long messagePing = message.getTimeCreated().toInstant().toEpochMilli() - event.getMessage().getTimeCreated().toInstant().toEpochMilli();
        message.editMessage(event.getAuthor().getAsMention() + ", Pong! Die Nachricht dauerte " + messagePing +  " ms. (Gateway: " + event.getJDA().getGatewayPing() + ", Api: " + event.getJDA().getRestPing().complete() + ")")
                .complete();
    }
}
