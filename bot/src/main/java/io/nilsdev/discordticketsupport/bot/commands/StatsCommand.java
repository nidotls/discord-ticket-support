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
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;
import java.util.List;

@CommandController
public class StatsCommand {

    @Command("stats")
    public void onCommand(CommandEvent event) {
        if(!this.isAuthorizedGuild(event.getGuild()) && !this.isAuthorizedMember(event.getMember())) {
            event.reply("Dieser Befehl geht nur auf dem `ni.ls` Discord. (`.ticket discord`)");
            return;
        }

        List<Guild> guilds = event.getJDA().getShardManager() == null
                ? event.getJDA().getGuilds()
                : event.getJDA().getShardManager().getGuilds();

        Integer memberCount = guilds
                .stream()
                .map(Guild::getMemberCount)
                .reduce(0, Integer::sum);

        int guildCount = guilds.size();

        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Statistiken")
                .addField("Gilden", String.valueOf(guildCount), true)
                .addField("Mitglieder", String.valueOf(memberCount), true)
                .addField("Entwickler", "nils#2488", true);

        event.reply(embedBuilder);
    }

    private boolean isAuthorizedGuild(Guild guild) {
        return guild.getIdLong() == 617339081168388110L;
    }

    private boolean isAuthorizedMember(@Nullable Member member) {
        if(member == null) return false;

        return member.getIdLong() == 210810160015212554L;
    }
}
