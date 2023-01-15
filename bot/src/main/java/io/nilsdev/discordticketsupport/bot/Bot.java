/*
 * MIT License
 *
 * Copyright (c) 2023 nils
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.nilsdev.discordticketsupport.bot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.nilsdev.discordticketsupport.bot.command.CommandListener;
import io.nilsdev.discordticketsupport.bot.command.CommandManager;
import io.nilsdev.discordticketsupport.bot.command.SlashCommandManager;
import io.nilsdev.discordticketsupport.bot.config.ConfigProperties;
import io.nilsdev.discordticketsupport.bot.listeners.TicketCloseListener;
import io.nilsdev.discordticketsupport.bot.listeners.TicketCreateListener;
import io.nilsdev.discordticketsupport.bot.listeners.TicketDeleteListener;
import io.nilsdev.discordticketsupport.bot.listeners.TicketOpenListener;
import io.nilsdev.discordticketsupport.bot.logging.AppLogger;
import io.nilsdev.discordticketsupport.bot.tasks.PresenceUpdateTask;
import io.nilsdev.discordticketsupport.bot.tasks.StatsTask;
import io.nilsdev.discordticketsupport.bot.utils.VersionUtil;
import io.nilsdev.discordticketsupport.common.config.Config;
import io.nilsdev.discordticketsupport.common.modules.CommonModule;
import io.nilsdev.discordticketsupport.common.repositories.StatsRepository;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

    @Getter
    private static Injector injector;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Logger logger;

    public Bot(String[] args) {
        AppLogger.create();
        this.logger = LogManager.getLogger("Bot");

        // ---

        ConfigProperties config = new ConfigProperties();

        // ---

        if (config.isDebug()) {
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final Configuration configuration = ctx.getConfiguration();
            configuration.getRootLogger().setLevel(Level.DEBUG);
            ctx.updateLoggers();
        }

        // ---

        if (config.isSentryEnabled()) {
            Sentry.init(options -> {
                options.setDsn(config.getSentryDsn());
                options.setRelease(VersionUtil.getVersion());
            });
        }

        // ---

        Config commonConfig = Config.builder()
                .databaseHost(config.getMongodbHost())
                .databasePort(config.getMongodbPort())
                .databaseUser(config.getMongodbUsername())
                .databasePassword(config.getMongodbPassword())
                .databaseName(config.getMongodbDatabase())
                .build();

        injector = Guice.createInjector(new CommonModule(commonConfig), new AbstractModule() {
            @Override
            protected void configure() {
                this.bind(CommandManager.class).to(SlashCommandManager.class);
            }
        });

        // ---

        ShardManager shardManager = DefaultShardManagerBuilder.createDefault(config.getDiscordToken())
                .setShardsTotal(config.getDiscordShardsTotal())
                .setShards(config.getDiscordShardsMin(), config.getDiscordShardsMax())
                .addEventListeners(injector.getInstance(TicketCloseListener.class))
                .addEventListeners(injector.getInstance(TicketCreateListener.class))
                .addEventListeners(injector.getInstance(TicketDeleteListener.class))
                .addEventListeners(injector.getInstance(TicketOpenListener.class))
                .addEventListeners(injector.getInstance(CommandListener.class))
                .build();

        // ---

        this.logger.info("Shard Status {}", shardManager.getStatuses().values());
        this.logger.info("Shard Info {}", shardManager.getShardById(0).getShardInfo().getShardString());
        this.logger.info("Logged in as {}", shardManager.getShardById(0).getSelfUser().getAsTag());

        // ---

        this.scheduler.scheduleAtFixedRate(new PresenceUpdateTask(shardManager), 0, 2, TimeUnit.MINUTES);
        this.scheduler.scheduleAtFixedRate(new StatsTask(shardManager, injector.getInstance(StatsRepository.class)), 5, 60, TimeUnit.MINUTES);
    }

    public static void main(String[] args) {
        new Bot(args);
    }
}
