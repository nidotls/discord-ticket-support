package io.nilsdev.discordticketsupport.bot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import com.google.inject.Inject;
import io.nilsdev.discordticketsupport.bot.utils.MessageUtil;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CommandController
public class ClearArchiveCommand {

    private final Logger logger = LogManager.getLogger("TicketCloseListener");

    private final GuildRepository guildRepository;

    @Inject
    public ClearArchiveCommand(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }

    @Command("cleararchive")
    public void onCommand(CommandEvent event) {
        Guild guild = event.getGuild();

        GuildModel guildModel = this.guildRepository.findByGuildId(guild.getId());

        // Check if guild exists
        if (guildModel == null) {
            event.reply("Ticket System ist nicht installiert!");
            this.logger.debug("Ignored guild null: {}", guildModel);
            return;
        }

        if(guildModel.getTicketArchiveCategoryId() == null) {
            event.reply("Ticket System ist nicht installiert!");
            this.logger.debug("Ignored guild ticketArchiveCategoryId null: {}", guildModel);
            return;
        }

        Category categoryById = guild.getCategoryById(guildModel.getTicketArchiveCategoryId());

        if(categoryById == null) {
            event.reply("Ticket Archiv existiert nicht!");
            this.logger.debug("Ignored guild ticketArchiveCategory null: {}", guildModel);
            return;
        }

        if (event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(guildModel.getTicketSupportPlusRoleId()))) {
            this.logger.debug("Ignored member has no support+ role: {}", event.getMember().getUser().getAsTag());

            MessageUtil.disposableMessage(this.logger, event.getChannel(), event.getMember().getUser().getAsMention() + ", du darfst das Archiv nicht löschen!");
            return;
        }

        for (GuildChannel channel : categoryById.getChannels()) {
            this.logger.debug("Deleting channel {} on {}", channel.getName(), guildModel);
            channel.delete().queue();
        }

        event.reply("Archiv geleert!");
    }
}
