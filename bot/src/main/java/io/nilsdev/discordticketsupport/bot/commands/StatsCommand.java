package io.nilsdev.discordticketsupport.bot.commands;

import io.nilsdev.discordticketsupport.bot.command.TicketCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class StatsCommand extends TicketCommand {
    public StatsCommand() {
        super("stats", "");
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

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
                .addField("Mitglieder", String.valueOf(memberCount), true);

        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
