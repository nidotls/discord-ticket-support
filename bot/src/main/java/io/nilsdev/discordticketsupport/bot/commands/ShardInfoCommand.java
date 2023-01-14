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
