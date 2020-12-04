package io.nilsdev.discordticketsupport.bot.utils;

import io.nilsdev.discordticketsupport.bot.Bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionUtil {

    public static String getVersion() {
        String version = "Unknown";

        try (InputStream inputStream = Bot.class.getClassLoader().getResourceAsStream("git.properties")) {
            Properties properties = new Properties();

            properties.load(inputStream);

            version = properties.getProperty("git.commit.id.describe");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }
}
