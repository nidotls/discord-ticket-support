package io.nilsdev.discordticketsupport.bot.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CommandListener extends ListenerAdapter {
    private final CommandManager commandManager;

    @Inject
    public CommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.commandManager.updateCommands(event.getJDA());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        TicketCommand command = this.commandManager.getCommand(event.getName());

        if (command == null) {
            // TODO: error message
            return;
        }

        command.process(event);
    }
}
