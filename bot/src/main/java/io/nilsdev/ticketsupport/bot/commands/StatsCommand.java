package io.nilsdev.ticketsupport.bot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import io.nilsdev.ticketsupport.bot.Bot;
import io.nilsdev.ticketsupport.common.models.GuildModel;
import io.nilsdev.ticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;
import java.util.Collections;

@CommandController
public class StatsCommand {

    @Command("stats")
    public void onCommand(CommandEvent event) {
        if(!event.getGuild().getId().equalsIgnoreCase("617339081168388110")) {
            event.reply("Dieser Befehl geht nur auf dem `ni.ls` Discord. (`.ticket discord`)");
            return;
        }

        Integer memberCount = event.getJDA().getGuilds()
                .stream()
                .map(Guild::getMemberCount)
                .reduce(0, Integer::sum);

        int guildCount = event.getJDA().getGuilds().size();

        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Statistiken")
                .addField("Gilden", String.valueOf(guildCount), true)
                .addField("Mitglieder", String.valueOf(memberCount), true)
                .addField("Entwickler", "nils#2488", true);

        event.reply(embedBuilder);
    }
}
