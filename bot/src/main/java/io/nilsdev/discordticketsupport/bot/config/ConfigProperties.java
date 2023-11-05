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

package io.nilsdev.discordticketsupport.bot.config;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class ConfigProperties {

    private final Logger logger = LoggerFactory.getLogger(ConfigProperties.class);
    private final boolean debug;
    private final String discordToken;
    private final int discordShardsTotal;
    private final int discordShardsMin;
    private final int discordShardsMax;
    private final String mongodbUri;

    private Properties properties;

    public ConfigProperties() {
        this.debug = getBoolean("DEBUG", false);
        this.discordToken = getString("DISCORD_TOKEN");
        this.discordShardsTotal = getInteger("DISCORD_SHARDS_TOTAL", 3);
        this.discordShardsMin = getInteger("DISCORD_SHARDS_MIN", 0);
        this.discordShardsMax = getInteger("DISCORD_SHARDS_MAX", 2);
        this.mongodbUri = getString("MONGODB_URI");
    }

    private String getString(String path) {
        return getString(path, null);
    }

    private String getString(String path, String def) {
        String env = System.getenv(path);

        if (env != null) {
            return env;
        }

        String property = getProperties().getProperty(path.replaceAll("_", ".").toLowerCase());

        if (property != null) {
            return property;
        }

        return def;
    }

    private Integer getInteger(String path) {
        return getInteger(path, null);
    }

    private Integer getInteger(String path, Integer def) {
        String env = System.getenv(path);

        if (env != null) {
            return Integer.valueOf(env);
        }

        String property = getProperties().getProperty(path.replaceAll("_", ".").toLowerCase());

        if (property != null) {
            return Integer.valueOf(property);
        }

        return def;
    }

    private Boolean getBoolean(String path) {
        return getBoolean(path, null);
    }

    private Boolean getBoolean(String path, Boolean def) {
        String env = System.getenv(path);

        if (env != null) {
            return Boolean.parseBoolean(env);
        }

        String property = getProperties().getProperty(path.replaceAll("_", ".").toLowerCase());

        if (property != null) {
            return Boolean.parseBoolean(property);
        }

        return def;
    }

    private Properties getProperties() {
        if (this.properties != null) {
            return this.properties;
        }

        this.properties = loadProperties();
        return this.properties;
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConfigProperties.class.getClassLoader().getResourceAsStream("bot.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                logger.error("Configuration file 'bot.properties' not found in resources.");
            }
        } catch (IOException ex) {
            logger.error("Error loading configuration file.", ex);
        }
        return properties;
    }
}
