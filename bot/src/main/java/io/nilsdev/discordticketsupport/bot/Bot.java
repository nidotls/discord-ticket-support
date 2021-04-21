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

package io.nilsdev.discordticketsupport.bot;

import com.github.kaktushose.jda.commands.entities.JDACommands;
import com.github.kaktushose.jda.commands.entities.JDACommandsBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.nilsdev.discordticketsupport.bot.command.EmbedFactory;
import io.nilsdev.discordticketsupport.bot.command.HelpMessageSender;
import io.nilsdev.discordticketsupport.bot.listeners.TicketCloseListener;
import io.nilsdev.discordticketsupport.bot.listeners.TicketCreateListener;
import io.nilsdev.discordticketsupport.bot.listeners.TicketDeleteListener;
import io.nilsdev.discordticketsupport.bot.listeners.TicketOpenListener;
import io.nilsdev.discordticketsupport.bot.logging.AppLogger;
import io.nilsdev.discordticketsupport.bot.tasks.PresenceUpdateTask;
import io.nilsdev.discordticketsupport.bot.utils.VersionUtil;
import io.nilsdev.discordticketsupport.common.config.Config;
import io.nilsdev.discordticketsupport.common.modules.CommonModule;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

    @Getter
    private static Injector injector;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Logger logger;

    public Bot(String[] args) throws LoginException, InterruptedException {
        AppLogger.create();
        this.logger = LogManager.getLogger("Bot");

        // ---

        File file = new File("bot.properties");

        if (!file.exists()) {
            try (InputStream input = Bot.class.getClassLoader().getResourceAsStream("bot.properties")) {

                Files.copy(Objects.requireNonNull(input), file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // ---

        if (properties.getProperty("debug", "false").equalsIgnoreCase("true")) {
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final Configuration config = ctx.getConfiguration();
            config.getRootLogger().setLevel(Level.DEBUG);
            ctx.updateLoggers();
        }

        // ---

        if (properties.getProperty("sentry.enabled", "false").equalsIgnoreCase("true")) {
            Sentry.init(options -> {
                options.setDsn(properties.getProperty("sentry.dsn"));
                options.setRelease(VersionUtil.getVersion());
            });
        } else {
            //Sentry.init(options -> {
            //    options.setDsn("");
            //    options.setRelease(VersionUtil.getVersion());
            //});
        }


        // ---

        Config config = Config.builder()
                .databaseHost(properties.getProperty("mongodb.host"))
                .databasePort(Integer.parseInt(properties.getProperty("mongodb.port")))
                .databaseUser(properties.getProperty("mongodb.database"))
                .databasePassword(properties.getProperty("mongodb.username"))
                .databaseName(properties.getProperty("mongodb.password"))
                .build();

        injector = Guice.createInjector(new CommonModule(config));

        // ---

        ShardManager shardManager = DefaultShardManagerBuilder.createDefault(properties.getProperty("discord.token"))
                .setShardsTotal(3)
                .setShards(0, 2)
                .addEventListeners(injector.getInstance(TicketCloseListener.class))
                .addEventListeners(injector.getInstance(TicketCreateListener.class))
                .addEventListeners(injector.getInstance(TicketDeleteListener.class))
                .addEventListeners(injector.getInstance(TicketOpenListener.class))
                .build();

        // ---

        JDACommands jdaCommands = new JDACommandsBuilder(shardManager)
                .setEmbedFactory(new EmbedFactory())
                .setHelpMessageSender(new HelpMessageSender())
                .build();

        jdaCommands.getSettings().setPrefix(".ticket ");
        jdaCommands.getSettings().setIgnoreBots(true);
        jdaCommands.getSettings().setIgnoreLabelCase(true);
        jdaCommands.getSettings().setBotMentionPrefix(true);

        // jdaCommands.getSettings().getHelpLabels().clear();

        // ---

        this.logger.info("Shard Status {}", shardManager.getStatuses().values());
        this.logger.info("Shard Info {}", shardManager.getShardById(0).getShardInfo().getShardString());
        this.logger.info("Logged in as {}", shardManager.getShardById(0).getSelfUser().getAsTag());

        // ---

        this.scheduler.scheduleAtFixedRate(new PresenceUpdateTask(shardManager), 0, 2, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new Bot(args);
    }
}
