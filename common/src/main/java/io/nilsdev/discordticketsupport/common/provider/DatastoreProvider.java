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

package io.nilsdev.discordticketsupport.common.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.nilsdev.discordticketsupport.common.config.Config;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.models.StatsModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class DatastoreProvider implements Provider<Datastore> {

    private final Logger logger = LogManager.getLogger(DatastoreProvider.class);

    private final MongoClient mongoClient;
    private final MongoClientURI mongoClientURI;

    @Inject
    public DatastoreProvider(Config config, MongoClient mongoClient) {
        this.mongoClient = mongoClient;

        this.mongoClientURI = new MongoClientURI(config.databaseUri());
    }

    @Override
    public Datastore get() {
        Datastore datastore = Morphia.createDatastore(this.mongoClient, Objects.requireNonNull(this.mongoClientURI.getDatabase()));

        try {
            datastore.getMapper().map(GuildModel.class, StatsModel.class);
        } catch (Throwable t) {
            this.logger.log(Level.ERROR, "Could not map models", t);
        }

        String modelPackage = "io.nilsdev.discordticketsupport.common.models";

        try {
            datastore.getMapper().mapPackage(modelPackage);
        } catch (Throwable t) {
            this.logger.warn("Could not map package " + modelPackage);
            // this.moduleConfig.getLogger().log(Level.SEVERE, "Could not map package " + this.moduleConfig.getModelPackage(), t);
        }

        try {
            datastore.ensureIndexes();
        } catch (Throwable t) {
            this.logger.log(Level.ERROR, "Could not ensure indexes", t);
        }

        return datastore;
    }
}