package io.nilsdev.discordticketsupport.bot.config;

import io.nilsdev.discordticketsupport.bot.Bot;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

@Getter
public class ConfigProperties {

    private final boolean debug;
    private final boolean sentryEnabled;
    private final String sentryDsn;
    private final String discordToken;
    private final int discordShardsTotal;
    private final int discordShardsMin;
    private final int discordShardsMax;
    private final String mongodbHost;
    private final int mongodbPort;
    private final String mongodbDatabase;
    private final String mongodbUsername;
    private final String mongodbPassword;

    private Properties properties;

    public ConfigProperties() {
        this.debug = this.getBoolean("DEBUG", false);
        this.sentryEnabled = this.getBoolean("SENTRY_ENABLED", false);
        this.sentryDsn = this.getString("SENTRY_DSN");
        this.discordToken = this.getString("DISCORD_TOKEN");
        this.discordShardsTotal = this.getInteger("DISCORD_SHARDS_TOTAL", 3);
        this.discordShardsMin = this.getInteger("DISCORD_SHARDS_MIN", 0);
        this.discordShardsMax = this.getInteger("DISCORD_SHARDS_MAX", 2);
        this.mongodbHost = this.getString("MONGODB_HOST");
        this.mongodbPort = this.getInteger("MONGODB_PORT");
        this.mongodbDatabase = this.getString("MONGODB_DATABASE");
        this.mongodbUsername = this.getString("MONGODB_USERNAME");
        this.mongodbPassword = this.getString("MONGODB_PASSWORD");
    }

    private String getString(String path) {
        return this.getString(path, null);
    }

    private String getString(String path, String def) {
        String env = System.getenv(path);

        if (env != null) {
            return env;
        }

        String property = this.getProperties().getProperty(path.replaceAll("_", ".").toLowerCase());

        if (property != null) {
            return property;
        }

        return def;
    }

    private Integer getInteger(String path) {
        return this.getInteger(path, null);
    }

    private Integer getInteger(String path, Integer def) {
        String env = System.getenv(path);

        if (env != null) {
            return Integer.valueOf(env);
        }

        String property = this.getProperties().getProperty(path.replaceAll("_", ".").toLowerCase());

        if (property != null) {
            return Integer.valueOf(property);
        }

        return def;
    }

    private Boolean getBoolean(String path) {
        return this.getBoolean(path, null);
    }

    private Boolean getBoolean(String path, Boolean def) {
        String env = System.getenv(path);

        if (env != null) {
            return Boolean.parseBoolean(env);
        }

        String property = this.getProperties().getProperty(path.replaceAll("_", ".").toLowerCase());

        if (property != null) {
            return Boolean.parseBoolean(property);
        }

        return def;
    }

    private Properties getProperties() {
        if (this.properties != null) {
            return this.properties;
        }

        File file = new File("bot.properties");
        Path path = file.toPath();

        if (!file.exists()) {
            try (InputStream input = Bot.class.getClassLoader().getResourceAsStream("bot.properties")) {

                Files.copy(Objects.requireNonNull(input), path);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        this.properties = new Properties();

        try (InputStream input = Files.newInputStream(path)) {
            this.properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return this.properties;
    }
}
