package io.nilsdev.discordticketsupport.bot.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.nilsdev.discordticketsupport.bot.command.TicketCommand;
import io.nilsdev.discordticketsupport.bot.utils.MessageUtil;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class ClearArchiveCommand extends TicketCommand {

    private final Logger logger = LogManager.getLogger(ClearArchiveCommand.class);

    private final GuildRepository guildRepository;

    @Inject
    public ClearArchiveCommand(GuildRepository guildRepository) {
        super("cleararchive", "");
        this.guildRepository = guildRepository;
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        Guild guild = event.getGuild();

        GuildModel guildModel = this.guildRepository.findByGuildId(guild.getId());

        // Check if guild exists
        if (guildModel == null) {
            event.getHook().sendMessage("Ticket System ist nicht installiert!").queue();
            this.logger.debug("Ignored guild null: {}", guild.getId());
            return;
        }

        if (guildModel.getTicketArchiveCategoryId() == null) {
            event.getHook().sendMessage("Ticket System ist nicht installiert!").queue();
            this.logger.debug("Ignored guild ticketArchiveCategoryId null: {}", guildModel);
            return;
        }

        Category categoryById = guild.getCategoryById(guildModel.getTicketArchiveCategoryId());

        if (categoryById == null) {
            event.getHook().sendMessage("Ticket Archiv existiert nicht!").queue();
            this.logger.debug("Ignored guild ticketArchiveCategory null: {}", guildModel);
            return;
        }

        if (event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(guildModel.getTicketSupportPlusRoleId()))) {
            this.logger.debug("Ignored member has no support+ role: {}", event.getMember().getUser().getAsTag());

            event.getHook().sendMessage("Du darfst das Archiv nicht löschen!").queue();
            return;
        }

        for (GuildChannel channel : categoryById.getChannels()) {
            this.logger.debug("Deleting channel {} on {}", channel.getName(), guildModel);
            channel.delete().queue();
        }

        event.getHook().sendMessage("Archiv geleert!").queue();
    }
}
