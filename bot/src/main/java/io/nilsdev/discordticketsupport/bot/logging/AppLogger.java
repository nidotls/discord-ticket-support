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

package io.nilsdev.discordticketsupport.bot.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.io.IoBuilder;

import java.util.logging.Handler;
import java.util.logging.Logger;

public final class AppLogger {

    private AppLogger() {
    }

    public static Logger create() {
        org.apache.logging.log4j.Logger redirect = LogManager.getRootLogger();
        System.setOut(IoBuilder.forLogger(redirect).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(redirect).setLevel(Level.ERROR).buildPrintStream());

        Logger root = Logger.getLogger("");
        root.setUseParentHandlers(false);

        // Remove existing handlers
        for (Handler handler : root.getHandlers()) {
            root.removeHandler(handler);
        }

        // Setup forward log handler
        root.setLevel(java.util.logging.Level.ALL); // Log4j will handle filtering the log
        root.addHandler(new Log4JLogHandler());

        return Logger.getLogger("Bot");
    }
}
