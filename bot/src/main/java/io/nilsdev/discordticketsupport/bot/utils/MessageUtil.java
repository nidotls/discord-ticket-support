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

package io.nilsdev.discordticketsupport.bot.utils;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
