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

package io.nilsdev.discordticketsupport.bot.tasks;

import com.google.inject.Inject;
import io.nilsdev.discordticketsupport.common.models.StatsModel;
import io.nilsdev.discordticketsupport.common.repositories.StatsRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.atomic.AtomicInteger;

public class StatsTask implements Runnable {

    private final ShardManager shardManager;
    private final StatsRepository statsRepository;

    @Inject
    public StatsTask(ShardManager shardManager, StatsRepository statsRepository) {
        this.shardManager = shardManager;
        this.statsRepository = statsRepository;
    }

    @Override
    public void run() {
        AtomicInteger totalGuilds = new AtomicInteger();
        AtomicInteger totalMembers = new AtomicInteger();

        this.shardManager.getShards().forEach(jda -> {
            for (Guild guild : jda.getGuilds()) {
                totalGuilds.incrementAndGet();
                totalMembers.addAndGet(guild.getMemberCount());
            }
        });

        StatsModel statsModel = StatsModel.builder()
                .guilds(totalGuilds.get())
                .members(totalMembers.get())
                .build();

        this.statsRepository.save(statsModel);
    }
}
