/*
 * Copyright (c) 2020 thenilsdev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 */

package io.nilsdev.discordticketsupport.bot.command;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandList;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class HelpMessageSender extends com.github.kaktushose.jda.commands.api.HelpMessageSender {

    public void sendDefaultHelp(GuildMessageReceivedEvent event, EmbedFactory embedFactory, CommandSettings settings, CommandList commands) {
        event.getChannel().sendMessage(getMessageEmbed()).queue();
    }

    public void sendSpecificHelp(GuildMessageReceivedEvent event, EmbedFactory embedFactory, CommandSettings settings, CommandCallable commandCallable) {
        event.getChannel().sendMessage(getMessageEmbed()).queue();
    }

    @NotNull
    public static MessageEmbed getMessageEmbed() {
        return new EmbedBuilder()
                    .setTitle("Alle Befehle vom ticket.ni.ls Bot")
                    .setColor(Color.RED)
                    .setDescription(""
                            + "**.ticket help**: Zeigt dir alle Befehle an.\n"
                            + "**.ticket invite**: Zeigt dir den Einladungs Link für den Bot an.\n"
                            + "**.ticket install**: Richtet den Bot auf deinem Discord-Server ein.\n"
                            + "**.ticket uninstall**: Entfernt den Bot von deinem Discord-Server.\n"
                            + "**.ticket ping**: Überprüft den Ping des Bot\"s zum Discord-Server.\n")
                    .setFooter("Made with ❤️ by nils#2488 (https://ni.ls)", "https://img.nilsdev.io/58bc4/5da0911b8077c.jpg")
                    .build();
    }
}
