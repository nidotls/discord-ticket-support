package io.nilsdev.discordticketsupport.bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class TicketCommand {

    private final String name;
    private final String description;

    protected TicketCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public abstract void process(SlashCommandInteractionEvent event);
}
