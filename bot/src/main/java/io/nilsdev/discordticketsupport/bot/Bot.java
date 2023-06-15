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

import ch.qos.logback.classic.Level;
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
import io.nilsdev.discordticketsupport.bot.tasks.PresenceUpdateTask;
import io.nilsdev.discordticketsupport.bot.tasks.StatsTask;
import io.nilsdev.discordticketsupport.common.config.Config;
import io.nilsdev.discordticketsupport.common.modules.CommonModule;
import io.nilsdev.discordticketsupport.common.repositories.StatsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Bot {

    @Getter
    private static Injector injector;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Bot(String[] args) {
        ConfigProperties config = new ConfigProperties();

        // ---

        if (config.isDebug()) {
            // hacky stuff https://stackoverflow.com/questions/10847458/how-to-enable-debug-in-slf4j-logger
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.DEBUG);
        }

        // ---

        Config commonConfig = Config.builder()
                .databaseUri(config.getMongodbUri())
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

        log.info("Shard Status {}", shardManager.getStatuses().values());
        log.info("Shard Info {}", shardManager.getShardById(0).getShardInfo().getShardString());
        log.info("Logged in as {}", shardManager.getShardById(0).getSelfUser().getName());

        // ---

        this.scheduler.scheduleAtFixedRate(new PresenceUpdateTask(shardManager), 0, 2, TimeUnit.MINUTES);
        this.scheduler.scheduleAtFixedRate(new StatsTask(shardManager, injector.getInstance(StatsRepository.class)), 5, 60, TimeUnit.MINUTES);
    }

    public static void main(String[] args) {
        new Bot(args);
    }
}
