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

package io.nilsdev.discordticketsupport.bot.commands;

import io.nilsdev.discordticketsupport.bot.command.TicketCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

public class ShardInfoCommand extends TicketCommand {
    public ShardInfoCommand() {
        super("shardinfo", "");
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

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

        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
