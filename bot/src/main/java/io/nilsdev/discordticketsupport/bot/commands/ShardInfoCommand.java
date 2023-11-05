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

import com.google.inject.Singleton;
import io.nilsdev.discordticketsupport.bot.command.TicketCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Singleton
public class ShardInfoCommand extends TicketCommand {
    public ShardInfoCommand() {
        super("shardinfo", "Zeigt Informationen über die Shards des Bots an");
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        final JDA jda = event.getJDA();
        final ShardManager shardManager = jda.getShardManager();
        final JDA.ShardInfo shardInfo = jda.getShardInfo();

        event.deferReply(true).queue();

        final EmbedBuilder embedBuilder = buildShardInfoEmbed(shardInfo, shardManager);
        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private EmbedBuilder buildShardInfoEmbed(JDA.ShardInfo shardInfo, ShardManager shardManager) {
        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Shard Info " + shardInfo.getShardString());

        shardManager.getStatuses().forEach((jda, status) -> {
            embedBuilder.addField("Shard " + jda.getShardInfo().getShardString(),
                String.format(" - Status: %s\n - Gilden: %d\n - Mitglieder: %d\n - Ping: %d",
                    status,
                    jda.getGuilds().size(),
                    jda.getGuilds().stream().mapToInt(Guild::getMemberCount).sum(),
                    jda.getGatewayPing()
                ), false
            );
        });

        return embedBuilder;
    }
}
