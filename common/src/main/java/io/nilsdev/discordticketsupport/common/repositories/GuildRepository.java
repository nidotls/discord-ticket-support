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

package io.nilsdev.discordticketsupport.common.repositories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.nilsdev.discordticketsupport.common.Constants;
import io.nilsdev.discordticketsupport.common.domain.Repository;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import xyz.morphia.Datastore;

@Singleton
public class GuildRepository extends Repository<GuildModel> {

    @Inject
    protected GuildRepository(@Named(Constants.DATASTORE) Datastore datastore) {
        super(GuildModel.class, datastore);
    }

    public GuildModel findByGuildId(String guildId) {
        return this.createQuery()
                .field("guildId").equal(guildId)
                .get();
    }
}
