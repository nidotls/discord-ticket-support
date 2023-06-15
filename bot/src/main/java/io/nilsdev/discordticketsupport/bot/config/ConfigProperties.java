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
    private final String discordToken;
    private final int discordShardsTotal;
    private final int discordShardsMin;
    private final int discordShardsMax;
    private final String mongodbUri;

    private Properties properties;

    public ConfigProperties() {
        this.debug = this.getBoolean("DEBUG", false);
        this.discordToken = this.getString("DISCORD_TOKEN");
        this.discordShardsTotal = this.getInteger("DISCORD_SHARDS_TOTAL", 3);
        this.discordShardsMin = this.getInteger("DISCORD_SHARDS_MIN", 0);
        this.discordShardsMax = this.getInteger("DISCORD_SHARDS_MAX", 2);
        this.mongodbUri = this.getString("MONGODB_URI");
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
