package io.nilsdev.discordticketsupport.bot.commands;

import io.nilsdev.discordticketsupport.bot.command.TicketCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class PingCommand extends TicketCommand {
    public PingCommand() {
        super("ping", "");
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Ping")
                .addField("Gateway", event.getJDA().getGatewayPing() + "ms", true)
                .addField("API", event.getJDA().getRestPing().complete() + "ms", true);

        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
