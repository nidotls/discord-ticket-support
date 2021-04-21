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

package io.nilsdev.discordticketsupport.bot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;
import java.util.List;

@CommandController
public class ShardInfoCommand {

    @Command("shardinfo")
    public void onCommand(CommandEvent event) {
        JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        ShardManager shardManager = event.getJDA().getShardManager();

        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Shard Info " + shardInfo.getShardString());

        shardManager.getStatuses().forEach((jda, status) -> embedBuilder.addField("Shard " + jda.getShardInfo().getShardString(),
                " - Status: " + status + "\n" +
                        " - Gilden: " + jda.getGuilds().size() + "\n" +
                        " - Mitglieder: " + jda.getGuilds().stream().mapToInt(Guild::getMemberCount).sum() + "\n" +
                        " - Ping: " + jda.getGatewayPing(),
                false)
        );

        event.reply(embedBuilder);
    }
}
