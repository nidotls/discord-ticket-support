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

package io.nilsdev.discordticketsupport.bot.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.nilsdev.discordticketsupport.bot.commands.*;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
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
