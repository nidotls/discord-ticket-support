/*
 * Copyright (c) 2020 thenilsdev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 */

package io.nilsdev.discordticketsupport.bot.listeners;

import com.google.inject.Inject;
import io.nilsdev.discordticketsupport.bot.utils.MessageUtil;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;
import java.util.Objects;

public class TicketOpenListener extends ListenerAdapter {

    private final Logger logger = LogManager.getLogger("TicketOpenListener");

    private final GuildRepository guildRepository;

    @Inject
    public TicketOpenListener(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        TextChannel channel = event.getGuildChannel().asTextChannel();

        // Filter Myself
        if (event.getJDA().getSelfUser().equals(event.getMember().getUser())) {
            this.logger.debug("Ignored self: {}", event.getMember().getUser().getAsTag());
            return;
        }

        // Check reaction emote
        UnicodeEmoji reaction = event.getEmoji().asUnicode();
        if (!reaction.getName().equals("\uD83D\uDD13")) {
            this.logger.debug("Ignored reaction: {}", reaction.getName());
            return;
        }

        if (channel.getParentCategory() == null) {
            this.logger.debug("Ignored no parent: {}", channel);
            return;
        }

        GuildModel guildModel = this.guildRepository.findByGuildId(event.getGuild().getId());

        // Check if guild exists
        if (guildModel == null) {
            this.logger.debug("Ignored guild null: {}", guildModel);
            return;
        }
        // Check if guild's ticket create channel
        if (!channel.getParentCategoryId().equals(guildModel.getTicketSupportCategoryId())
                && !channel.getParentCategoryId().equals(guildModel.getTicketArchiveCategoryId())) {
            this.logger.debug("Ignored parent id does not match: {} != {} && {} != {}", channel.getParentCategoryId(), guildModel.getTicketSupportCategoryId(), channel.getParentCategoryId(), guildModel.getTicketArchiveCategoryId());
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

        if (channel.getParentCategoryId().equals(guildModel.getTicketSupportCategoryId())) {
            this.logger.debug("Ignored because already opened: {}", channel.getId());
            return;
        }

        // Support Ban
        if (event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(guildModel.getTicketSupportRoleId()) || role.getId().equals(guildModel.getTicketSupportPlusRoleId()))) {
            this.logger.debug("Ignored member has no support role: {}", event.getMember().getUser().getAsTag());

            MessageUtil.disposableMessage(this.logger, channel, event.getMember().getUser().getAsMention() + ", du darfst keine Tickets öffnen!");
            return;
        }

        Category supportCategory = event.getJDA().getCategoryById(guildModel.getTicketSupportCategoryId());

        if (supportCategory == null || supportCategory.getChannels().size() >= 50) {
            this.logger.debug("Ignored too many tickets: {}", supportCategory);

            MessageUtil.disposableMessage(this.logger, channel, event.getMember().getUser().getAsMention() + ", Ticket konnte nicht geöffnet werden, Kategorie voll!");
            return;
        }

        channel.sendMessage(event.getUser().getAsMention() + " hat das Ticket geöffnet!").complete();

        channel.getManager().setParent(supportCategory).complete();

        try {
            Member memberById = event.getGuild().retrieveMemberById(Objects.requireNonNull(channel.getTopic())).complete();

            channel.upsertPermissionOverride(Objects.requireNonNull(memberById))
                    .grant(Permission.VIEW_CHANNEL)
                    .grant(Permission.MESSAGE_SEND)
                    .complete();
        } catch (NullPointerException ignored) {}

        // Log

        if (guildModel.getTicketLogTextChannelId() == null) return;

        TextChannel logTextChannel = event.getGuild().getTextChannelById(guildModel.getTicketLogTextChannelId());

        if(logTextChannel == null) return;

        EmbedBuilder embedBuilder = new EmbedBuilder();

        String userMention;

        try {
            User user = event.getJDA().retrieveUserById(Objects.requireNonNull(channel.getTopic())).complete();
            userMention = user == null ? "Undefined" : user.getAsMention();
        } catch (Exception e) {
            e.printStackTrace();
            userMention = "Undefined";
        }

        embedBuilder.setTitle("Reopened Ticket `" + channel.getName() + "`");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.addField("Ticket", channel.getAsMention(), false);
        embedBuilder.addField("Supporter", event.getUser().getAsMention(), true);
        embedBuilder.addField("User", userMention , true);
        embedBuilder.setTimestamp(new Date().toInstant());

        logTextChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
