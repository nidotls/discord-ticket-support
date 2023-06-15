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

package io.nilsdev.discordticketsupport.bot.listeners;

import com.google.inject.Inject;
import io.nilsdev.discordticketsupport.bot.utils.MessageUtil;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TicketCreateListener extends ListenerAdapter {

    private final GuildRepository guildRepository;

    @Inject
    public TicketCreateListener(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        TextChannel channel = event.getGuildChannel().asTextChannel();

        // Filter Myself
        if (event.getJDA().getSelfUser().equals(event.getMember().getUser())) {
            log.debug("Ignored self: {}", event.getMember().getUser().getName());
            return;
        }

        // Check reaction emote
        UnicodeEmoji reaction = event.getEmoji().asUnicode();
        if (!reaction.getName().equals("\uD83D\uDCE9")) {
            log.debug("Ignored reaction: {}", reaction.getName());
            return;
        }

        GuildModel guildModel = this.guildRepository.findByGuildId(event.getGuild().getId());

        // Check if guild exists
        if (guildModel == null) {
            log.debug("Ignored guild null: {}", guildModel);
            return;
        }

        // Check if guild's ticket create channel
        if (!channel.getId().equals(guildModel.getTicketCreateTextChannelId())) {
            log.debug("Ignored getTicketCreateTextChannelId does not match: {} != {}", channel.getId(), guildModel.getTicketCreateTextChannelId());
            return;
        }

        // Remove Reaction
        try {
            List<User> users = event.getReaction().retrieveUsers().complete();
            log.info("[" + event.getGuild() + "] Remove reaction from " + event.getUser().toString() + " {count: " + users.size() + ", users: \"" + users.stream().map(Object::toString).collect(Collectors.joining(", ")) + "\"}");
        } catch (Throwable t) {
            log.error("[" + event.getGuild() + "] Could not generate debug", t);
        }

        event.getReaction().removeReaction(event.getUser()).submit().whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
                log.error(event.getGuild().toString(), throwable);
                return;
            }

            event.retrieveMessage().queue(message -> {
                List<MessageReaction> reactions = message.getReactions();

                // ---

                List<MessageReaction> otherReactions = reactions.stream()
                        .filter(messageReaction -> !messageReaction.getEmoji().asUnicode().getName().equals("\uD83D\uDCE9"))
                        .collect(Collectors.toList());

                if (!otherReactions.isEmpty()) {
                    log.warn("[" + event.getGuild() + "] Found other reactions: " + otherReactions.size());

                    for (MessageReaction otherReaction : otherReactions) {
                        log.warn("[" + event.getGuild() + "] Remove reaction " + otherReaction.toString() + "(" + reaction.getName() + "|" + reaction.getFormatted() + ")");
                        otherReaction.clearReactions().queue();
                    }
                }

                // ---

                message.retrieveReactionUsers(Emoji.fromUnicode("\uD83D\uDCE9")).queue(users -> {
                    if (users.size() != 1) {
                        log.info("[" + event.getGuild() + "] Reaction is not set: " + users.size());
                        log.info("[" + event.getGuild() + "] Add ticket open reaction, because it was missing");
                        message.addReaction(Emoji.fromUnicode("\uD83D\uDCE9")).queue();
                    }

                    log.info("[" + event.getGuild() + "] Removed reaction from " + event.getUser().toString() + " {count: " + users.size() + ", users: \"" + users.stream().map(Object::toString).collect(Collectors.joining(", ")) + "\"}");
                });

                log.debug("Reaction removed successfully");
            });
        });

        // Filter Bots
        if (event.getMember().getUser().isBot()) {
            log.debug("Ignored bot: {}", event.getMember().getUser().getName());
            return;
        }

        // Support Ban
        if (event.getMember().getRoles().stream().anyMatch(role -> role.getId().equals(guildModel.getTicketSupportBanRoleId()))) {
            log.debug("Ignored member hast ban role: {}", event.getMember().getUser().getName());

            MessageUtil.disposableMessage(log, channel, event.getMember().getUser().getAsMention() + ", du darfst keine Tickets erstellen!");
            return;
        }

        Category category = event.getGuild().getCategoryById(guildModel.getTicketSupportCategoryId());

        if (category == null || category.getChannels().size() >= 50) {
            log.debug("Ignored too many tickets: {}", category);

            MessageUtil.disposableMessage(log, channel, event.getMember().getUser().getAsMention() + ", derzeit sind zu viele Tickets offen, probiere es in ein paar Minuten erneut!");
            return;
        }

        Optional<TextChannel> textChannel = category.getTextChannels().stream().filter(guildChannel -> Objects.equals(guildChannel.getTopic(), event.getUser().getId())).findFirst();

        if (textChannel.isPresent()) {
            log.debug("Ignored already opened ticket: {}", textChannel.get().getAsMention());

            MessageUtil.disposableMessage(log, channel, event.getMember().getUser().getAsMention() + ", du hast bereits ein offenes Ticket, " + textChannel.get().getAsMention());
            return;
        }

        String ticketId = UUID.randomUUID().toString().substring(0, 4);

        TextChannel ticketTextChannel = event.getGuild().createTextChannel("\uD83C\uDFAB-" + ticketId)
                .setParent(category)
                //.setPosition(0)
                .setTopic(event.getUser().getId())
                .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(),
                        Collections.emptyList(),
                        Collections.singletonList(
                                Permission.VIEW_CHANNEL
                        )
                )
                .addRolePermissionOverride(Long.parseLong(guildModel.getTicketSupportRoleId()),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_SEND,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Collections.emptyList()
                )
                .addRolePermissionOverride(Long.parseLong(guildModel.getTicketSupportPlusRoleId()),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_SEND,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Collections.emptyList()
                )
                .addMemberPermissionOverride(event.getUser().getIdLong(),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_SEND,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Collections.emptyList()
                )
                .setSlowmode(5)
                .complete();

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setTitle("ticket.ni.ls")
                .setColor(Color.RED)
                .setDescription("Ticket von " + event.getUser().getAsMention() + "\n:lock: - schließen\n:unlock: - öffnen\n:no_entry_sign: - löschen")
                .build();

        Message message = ticketTextChannel.sendMessageEmbeds(messageEmbed).complete();

        message.addReaction(Emoji.fromUnicode("\uD83D\uDD12")).complete();
        message.addReaction(Emoji.fromUnicode("\uD83D\uDD13")).complete();
        message.addReaction(Emoji.fromUnicode("\uD83D\uDEAB")).complete();

        message.pin().complete();

        ticketTextChannel.sendMessage("Hey " + event.getUser().getAsMention() + ", bitte schildere kurz dein Problem, damit dir ein Supporter helfen kann!").complete();

        // Log

        if (guildModel.getTicketLogTextChannelId() == null) return;

        TextChannel logTextChannel = event.getGuild().getTextChannelById(guildModel.getTicketLogTextChannelId());

        if (logTextChannel == null) return;

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Opened Ticket `" + ticketTextChannel.getName() + "`");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.addField("Ticket", ticketTextChannel.getAsMention(), false);
        embedBuilder.addField("User", event.getUser().getAsMention(), true);
        embedBuilder.setTimestamp(new Date().toInstant());

        logTextChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
