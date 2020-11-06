package io.nilsdev.ticketsupport.bot.listeners;

import com.google.inject.Inject;
import io.nilsdev.ticketsupport.common.models.GuildModel;
import io.nilsdev.ticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TicketCreateListener extends ListenerAdapter {

    private final Logger logger = LogManager.getLogger("TicketCreateListener");

    private final GuildRepository guildRepository;

    @Inject
    public TicketCreateListener(GuildRepository guildRepository) {
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
        if (!event.getReactionEmote().getName().equals("\uD83D\uDCE9")) {
            this.logger.debug("Ignored reaction: {}", event.getReactionEmote().getName());
            return;
        }

        GuildModel guildModel = this.guildRepository.findByGuildId(event.getGuild().getId());

        // Check if guild exists
        if (guildModel == null) {
            this.logger.debug("Ignored guild null: {}", guildModel);
            return;
        }

        // Check if guild's ticket create channel
        if (!event.getChannel().getId().equals(guildModel.getTicketCreateTextChannelId())) {
            this.logger.debug("Ignored getTicketCreateTextChannelId does not match: {} != {}", event.getChannel().getId(), guildModel.getTicketCreateTextChannelId());
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

        // Support Ban
        if (event.getMember().getRoles().stream().anyMatch(role -> role.getId().equals(guildModel.getTicketSupportBanRoleId()))) {
            this.logger.debug("Ignored member hast ban role: {}", event.getMember().getUser().getAsTag());

            CompletableFuture<Message> messageCompletableFuture = event.getChannel().sendMessage(event.getMember().getUser().getAsMention() + ", du darfst keine Tickets erstellen!").submit();

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

        Category category = event.getGuild().getCategoryById(guildModel.getTicketSupportCategoryId());

        if (category == null || category.getChannels().size() >= 50) {
            this.logger.debug("Ignored too many tickets: {}", category);

            CompletableFuture<Message> messageCompletableFuture = event.getChannel().sendMessage(event.getMember().getUser().getAsMention() + ", derzeit sind zu viele Tickets offen, probiere es in ein paar Minuten erneut!").submit();

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

        Optional<TextChannel> textChannel = category.getTextChannels().stream().filter(guildChannel -> Objects.equals(guildChannel.getTopic(), event.getUser().getId())).findFirst();

        if (textChannel.isPresent()) {
            this.logger.debug("Ignored already opened ticket: {}", textChannel.get().getAsMention());

            CompletableFuture<Message> messageCompletableFuture = event.getChannel().sendMessage(event.getMember().getUser().getAsMention() + ", du hast bereits ein offenes Ticket, " + textChannel.get().getAsMention()).submit();

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

        String ticketId = UUID.randomUUID().toString().substring(0, 4);

        TextChannel ticketTextChannel = event.getGuild().createTextChannel("\uD83C\uDFAB-" + ticketId, category)
                .setPosition(0)
                .setTopic(event.getUser().getId())
                .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(),
                        Collections.emptyList(),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_READ
                        )
                )
                .addRolePermissionOverride(Long.parseLong(guildModel.getTicketSupportRoleId()),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_READ,
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Collections.emptyList()
                )
                .addRolePermissionOverride(Long.parseLong(guildModel.getTicketSupportPlusRoleId()),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_READ,
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Collections.emptyList()
                )
                .addMemberPermissionOverride(event.getUser().getIdLong(),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_READ,
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Collections.emptyList()
                )
                .setSlowmode(5)
                .complete();

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setTitle("ticket.ni.ls")
                .setColor(0xff0000)
                .setDescription("Ticket von " + event.getUser().getAsMention() + "\n:lock: - schließen\n:unlock: - öffnen\n:no_entry_sign: - löschen")
                .build();

        Message message = ticketTextChannel.sendMessage(messageEmbed).complete();

        message.addReaction("\uD83D\uDD12").complete();
        message.addReaction("\uD83D\uDD13").complete();
        message.addReaction("\uD83D\uDEAB").complete();

        message.pin().complete();

        ticketTextChannel.sendMessage("Hey " + event.getUser().getAsMention() + ", bitte schildere kurz dein Problem, damit dir ein Supporter helfen kann!").complete();
    }
}
