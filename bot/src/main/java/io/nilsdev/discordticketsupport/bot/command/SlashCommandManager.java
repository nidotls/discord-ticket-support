package io.nilsdev.discordticketsupport.bot.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.nilsdev.discordticketsupport.bot.commands.*;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class SlashCommandManager implements CommandManager {

    private final GuildRepository guildRepository;
    private final Map<String, TicketCommand> commandMap = new HashMap<>();

    @Inject
    public SlashCommandManager(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;

        // global
        this.registerCommand(new StatsCommand());
        this.registerCommand(new ShardInfoCommand());
        this.registerCommand(new PingCommand());

        // guild
        this.registerCommand(new InstallCommand(guildRepository));
        this.registerCommand(new UninstallCommand(guildRepository));
        this.registerCommand(new ClearArchiveCommand(guildRepository));
    }

    @Override
    public void updateCommands(JDA jda) {
        jda.upsertCommand(Commands.slash("install", "Installiert das Ticket System")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                )
                .queue();
        jda.upsertCommand(Commands.slash("stats", "Zeigt Statistiken")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                )
                .queue();
        jda.upsertCommand(Commands.slash("shardinfo", "Zeigt Shard Info")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                )
                .queue();
        jda.upsertCommand(Commands.slash("ping", "Zeigt den Ping vom Bot an")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                )
                .queue();

        jda.upsertCommand(Commands.slash("uninstall", "Deinstalliert das Ticket System")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                )
                .queue();
        jda.upsertCommand(Commands.slash("cleararchive", "Leert das Archiv")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                )
                .queue();
    }

    @Override
    public void registerCommand(TicketCommand command) {
        commandMap.put(command.getName(), command);
    }

    @Nullable
    @Override
    public TicketCommand getCommand(String name) {
        return this.commandMap.get(name);
    }
}
