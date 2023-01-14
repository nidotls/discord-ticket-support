package io.nilsdev.discordticketsupport.bot.command;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

public interface CommandManager {

    void updateCommands(JDA jda);

    void registerCommand(TicketCommand command);

    @Nullable TicketCommand getCommand(String name);
}
