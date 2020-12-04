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

package io.nilsdev.discordticketsupport.bot.commands;

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
