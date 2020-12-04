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
