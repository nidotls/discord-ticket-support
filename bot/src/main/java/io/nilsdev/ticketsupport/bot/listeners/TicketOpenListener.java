package io.nilsdev.ticketsupport.bot.listeners;

import com.google.inject.Inject;
import io.nilsdev.ticketsupport.bot.utils.MessageUtil;
import io.nilsdev.ticketsupport.common.models.GuildModel;
import io.nilsdev.ticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TicketOpenListener extends ListenerAdapter {

    private final Logger logger = LogManager.getLogger("TicketOpenListener");

    private final GuildRepository guildRepository;

    @Inject
    public TicketOpenListener(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        // Filter Myself
        if (event.getJDA().getSelfUser().equals(event.getMember().getUser())) {
            this.logger.debug("Ignored self: {}", event.getMember().getUser().getAsTag());
            return;
        }

        // Check reaction emote
        if (!event.getReactionEmote().getName().equals("\uD83D\uDD13")) {
            this.logger.debug("Ignored reaction: {}", event.getReactionEmote().getName());
            return;
        }

        if (event.getChannel().getParent() == null) {
            this.logger.debug("Ignored no parent: {}", event.getChannel().getParent());
            return;
        }

        GuildModel guildModel = this.guildRepository.findByGuildId(event.getGuild().getId());

        // Check if guild exists
        if (guildModel == null) {
            this.logger.debug("Ignored guild null: {}", guildModel);
            return;
        }
        // Check if guild's ticket create channel
        if (!event.getChannel().getParent().getId().equals(guildModel.getTicketSupportCategoryId())
                && !event.getChannel().getParent().getId().equals(guildModel.getTicketArchiveCategoryId())) {
            this.logger.debug("Ignored parent id does not match: {} != {} && {} != {}", event.getChannel().getParent().getId(), guildModel.getTicketSupportCategoryId(), event.getChannel().getParent().getId(), guildModel.getTicketArchiveCategoryId());
            return;
        }

        // Remove Reaction
        event.getReaction().removeReaction(event.getUser()).submit().whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
                this.logger.throwing(throwable);
                return;
            }

            this.logger.debug("Reaction removed successfully");
        });

        // Filter Bots
        if (event.getMember().getUser().isBot()) {
            this.logger.debug("Ignored bot: {}", event.getMember().getUser().getAsTag());
            return;
        }

        if (event.getChannel().getParent().getId().equals(guildModel.getTicketSupportCategoryId())) {
            this.logger.debug("Ignored because already opened: {}", event.getChannel().getId());
            return;
        }

        // Support Ban
        if (event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(guildModel.getTicketSupportRoleId()) || role.getId().equals(guildModel.getTicketSupportPlusRoleId()))) {
            this.logger.debug("Ignored member has no support role: {}", event.getMember().getUser().getAsTag());

            MessageUtil.disposableMessage(this.logger, event.getChannel(), event.getMember().getUser().getAsMention() + ", du darfst keine Tickets öffnen!");
            return;
        }

        Category supportCategory = event.getJDA().getCategoryById(guildModel.getTicketSupportCategoryId());

        if (supportCategory == null || supportCategory.getChannels().size() >= 50) {
            this.logger.debug("Ignored too many tickets: {}", supportCategory);

            MessageUtil.disposableMessage(this.logger, event.getChannel(), event.getMember().getUser().getAsMention() + ", Ticket konnte nicht geöffnet werden, Kategorie voll!");
            return;
        }

        event.getChannel().getManager().setParent(supportCategory).complete();

        Member memberById = event.getGuild().retrieveMemberById(Objects.requireNonNull(event.getChannel().getTopic())).complete();

        event.getChannel().upsertPermissionOverride(Objects.requireNonNull(memberById))
                .grant(Permission.MESSAGE_WRITE)
                .complete();

        event.getChannel().sendMessage(event.getUser().getAsMention() + " hat das Ticket geöffnet!").complete();
    }
}
