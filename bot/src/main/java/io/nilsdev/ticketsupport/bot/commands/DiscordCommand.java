package io.nilsdev.ticketsupport.bot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@CommandController
public class DiscordCommand {

    @Command("discord")
    public void onCommand(CommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Support Discord", "https://dc.ni.ls")
                .setColor(Color.RED)
                .setFooter("Made with ❤️ by nils#2488 (https://ni.ls)", "https://img.nilsdev.io/58bc4/5da0911b8077c.jpg");

        event.reply(embedBuilder);
    }
}
