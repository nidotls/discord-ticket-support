package io.nilsdev.ticketsupport.bot.listeners;

import com.google.inject.Inject;
import io.nilsdev.ticketsupport.common.models.GuildModel;
import io.nilsdev.ticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TicketCloseListener extends ListenerAdapter {

    private final Logger logger = LogManager.getLogger("TicketCloseListener");

    private final GuildRepository guildRepository;

    @Inject
    public TicketCloseListener(GuildRepository guildRepository) {
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
        if (!event.getReactionEmote().getName().equals("\uD83D\uDD12")) {
            this.logger.debug("Ignored reaction: {}", event.getReactionEmote().getName());
            return;
        }

        if(event.getChannel().getParent() == null) {
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

        if(event.getChannel().getParent().getId().equals(guildModel.getTicketArchiveCategoryId())) {
            this.logger.debug("Ignored because already closed: {}", event.getChannel().getId());
            return;
        }

        // Support Ban
        if (event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(guildModel.getTicketSupportRoleId()) || role.getId().equals(guildModel.getTicketSupportPlusRoleId()))) {
            this.logger.debug("Ignored member has no support role: {}", event.getMember().getUser().getAsTag());

            CompletableFuture<Message> messageCompletableFuture = event.getChannel().sendMessage(event.getMember().getUser().getAsMention() + ", du darfst keine Tickets schließen!").submit();

            messageCompletableFuture.whenCompleteAsync((message, throwable) -> {
                if (throwable != null) {
                    this.logger.throwing(throwable);
                    return;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                message.delete().submit();
            });
            return;
        }

        Category archiveCategory = event.getJDA().getCategoryById(guildModel.getTicketArchiveCategoryId());

        if(archiveCategory == null || archiveCategory.getChannels().size() >= 50) {
            this.logger.debug("Ignored too many tickets: {}", archiveCategory);

            CompletableFuture<Message> messageCompletableFuture = event.getChannel().sendMessage(event.getMember().getUser().getAsMention() + ", Ticket konnte nicht geschlossen werden, Archiv voll!").submit();

            messageCompletableFuture.whenCompleteAsync((message, throwable) -> {
                if (throwable != null) {
                    this.logger.throwing(throwable);
                    return;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                message.delete().submit();
            });
            return;
        }

        event.getChannel().getManager().setParent(archiveCategory).complete();

        Member memberById = event.getGuild().getMemberById(Objects.requireNonNull(event.getChannel().getTopic()));

        event.getChannel().upsertPermissionOverride(Objects.requireNonNull(memberById))
                .deny(Permission.MESSAGE_WRITE)
                .complete();

        event.getChannel().sendMessage(event.getUser().getAsMention() + " hat das Ticket geschlossen!").complete();
    }
}
