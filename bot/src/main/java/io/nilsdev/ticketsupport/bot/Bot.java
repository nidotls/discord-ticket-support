package io.nilsdev.ticketsupport.bot;

import com.github.kaktushose.jda.commands.entities.JDACommands;
import com.github.kaktushose.jda.commands.entities.JDACommandsBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.nilsdev.ticketsupport.bot.command.EmbedFactory;
import io.nilsdev.ticketsupport.bot.command.HelpMessageSender;
import io.nilsdev.ticketsupport.bot.listeners.TicketCloseListener;
import io.nilsdev.ticketsupport.bot.listeners.TicketCreateListener;
import io.nilsdev.ticketsupport.bot.listeners.TicketDeleteListener;
import io.nilsdev.ticketsupport.bot.listeners.TicketOpenListener;
import io.nilsdev.ticketsupport.bot.logging.AppLogger;
import io.nilsdev.ticketsupport.bot.tasks.PresenceUpdateTask;
import io.nilsdev.ticketsupport.common.config.Config;
import io.nilsdev.ticketsupport.common.modules.CommonModule;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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

        Config config = Config.builder()
                .databaseHost(properties.getProperty("mongodb.host", "db.host01.iwmedia.dev"))
                .databasePort(Integer.parseInt(properties.getProperty("mongodb.port", "27017")))
                .databaseUser(properties.getProperty("mongodb.database", "node-ticket-ni-ls"))
                .databasePassword(properties.getProperty("mongodb.username", "node-ticket-ni-ls"))
                .databaseName(properties.getProperty("mongodb.password", "node-ticket-ni-ls"))
                .build();

        injector = Guice.createInjector(new CommonModule(config));

        // ---

        JDA jda = JDABuilder.createDefault(properties.getProperty("discord.token"))
                .addEventListeners(injector.getInstance(TicketCloseListener.class))
                .addEventListeners(injector.getInstance(TicketCreateListener.class))
                .addEventListeners(injector.getInstance(TicketDeleteListener.class))
                .addEventListeners(injector.getInstance(TicketOpenListener.class))
                .build();

        jda.awaitReady();

        // ---

        JDACommands jdaCommands = new JDACommandsBuilder(jda)
                .setEmbedFactory(new EmbedFactory())
                .setHelpMessageSender(new HelpMessageSender())
                .build();

        jdaCommands.getSettings().setPrefix(".ticket ");
        jdaCommands.getSettings().setIgnoreBots(true);
        jdaCommands.getSettings().setIgnoreLabelCase(true);
        jdaCommands.getSettings().setBotMentionPrefix(true);

        // jdaCommands.getSettings().getHelpLabels().clear();

        // ---

        this.logger.info("Shard Status {}", jda.getShardInfo().getShardString());
        this.logger.info("Logged in as {}", jda.getSelfUser().getAsTag());

        // ---

        this.scheduler.scheduleAtFixedRate(new PresenceUpdateTask(jda), 0, 2, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new Bot(args);
    }
}
