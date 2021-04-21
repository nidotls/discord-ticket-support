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
import io.nilsdev.discordticketsupport.bot.utils.VersionUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

public class PresenceUpdateTask implements Runnable {

    private final ShardManager shardManager;

    @Inject
    public PresenceUpdateTask(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void run() {
        String activity = ".ticket help | Version " + VersionUtil.getVersion();

        this.shardManager.getShards().forEach(jda -> {
            JDA.ShardInfo shardInfo = jda.getShardInfo();

            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(activity + " | Shard " + shardInfo.getShardId() + "/" + shardInfo.getShardTotal()));
        });
    }
}
