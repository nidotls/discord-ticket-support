package io.nilsdev.ticketsupport.bot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import io.nilsdev.ticketsupport.bot.Bot;
import io.nilsdev.ticketsupport.common.models.GuildModel;
import io.nilsdev.ticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

@CommandController
public class InviteCommand {

    @Command("invite")
    public void onCommand(CommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Einladungs Link", "https://discordapp.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot")
                .setColor(Color.RED)
                .setFooter("Made with ❤️ by nils#2488 (https://ni.ls)", "https://img.nilsdev.io/58bc4/5da0911b8077c.jpg");

        event.reply(embedBuilder);
    }
}
