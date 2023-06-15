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

package io.nilsdev.discordticketsupport.bot.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.nilsdev.discordticketsupport.bot.command.TicketCommand;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Singleton
public class InstallCommand extends TicketCommand {

    private final GuildRepository guildRepository;

    @Inject
    public InstallCommand(GuildRepository guildRepository) {
        super("install", "");

        this.guildRepository = guildRepository;
    }

    @Override
    public void process(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage(event.getMember().getAsMention() + ", der Befehl install erfordert, dass du die Berechtigung \"Administrator\" hast.").queue();
            return;
        }

        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage(event.getMember().getAsMention() + ", ich brauche die Berechtigung \"Administrator\", damit der Befehl \\`install\\` funktioniert.`").queue();
            return;
        }

        GuildModel guildModel = this.guildRepository.findByGuildId(event.getGuild().getId());

        if (guildModel == null) {
            guildModel = GuildModel.builder()
                    .guildId(event.getGuild().getId())
                    .build();
            this.guildRepository.save(guildModel);
        }

        // ---

        String log = "**Rollen:**\n";

        try {
            Role roleById = guildModel.getTicketSupportRoleId() != null
                    ? event.getGuild().getRoleById(guildModel.getTicketSupportRoleId())
                    : null;
            if (roleById == null) {
                boolean shouldExists = guildModel.getTicketSupportRoleId() != null;

                Role role = event.getGuild().createRole()
                        .setName("Ticket Support")
                        .setMentionable(false)
                        .reason("Requested by user")
                        .complete();

                guildModel.setTicketSupportRoleId(role.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: `" + role.getName() + "` wurde neu erstellt\n"
                        : ":star: `" + role.getName() + "` wurde initial erstellt\n";
            } else {
                log += ":white_check_mark: `" + roleById.getName() + "` existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `Ticket Support` konnte nicht erstellt werden\n";
        }

        try {
            Role roleById = guildModel.getTicketSupportPlusRoleId() != null
                    ? event.getGuild().getRoleById(guildModel.getTicketSupportPlusRoleId())
                    : null;
            if (roleById == null) {
                boolean shouldExists = guildModel.getTicketSupportPlusRoleId() != null;

                Role role = event.getGuild().createRole()
                        .setName("Ticket Support+")
                        .setMentionable(false)
                        .reason("Requested by user")
                        .complete();

                guildModel.setTicketSupportPlusRoleId(role.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: `" + role.getName() + "` wurde neu erstellt\n"
                        : ":star: `" + role.getName() + "` wurde initial erstellt\n";
            } else {
                log += ":white_check_mark: `" + roleById.getName() + "` existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `Ticket Support+` konnte nicht erstellt werden\n";
        }

        try {
            Role roleById = guildModel.getTicketSupportBanRoleId() != null
                    ? event.getGuild().getRoleById(guildModel.getTicketSupportBanRoleId())
                    : null;
            if (roleById == null) {
                boolean shouldExists = guildModel.getTicketSupportBanRoleId() != null;

                Role role = event.getGuild().createRole()
                        .setName("Ticket Sperre")
                        .setMentionable(false)
                        .reason("Requested by user")
                        .complete();

                guildModel.setTicketSupportBanRoleId(role.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: `" + role.getName() + "` wurde neu erstellt\n"
                        : ":star: `" + role.getName() + "` wurde initial erstellt\n";
            } else {
                log += ":white_check_mark: `" + roleById.getName() + "` existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `Ticket Sperre` konnte nicht erstellt werden\n";
        }

        // ---

        log += "\n**Kategorien:**\n";
        Category supportCategory = null;
        try {
            Category categoryById = guildModel.getTicketSupportCategoryId() != null
                    ? event.getGuild().getCategoryById(guildModel.getTicketSupportCategoryId())
                    : null;
            if (categoryById == null) {
                boolean shouldExists = guildModel.getTicketSupportCategoryId() != null;

                Category category = event.getGuild().createCategory("\uD83C\uDFAB Support Tickets")
                        .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(),
                                Arrays.asList(
                                        Permission.VIEW_CHANNEL,
                                        Permission.MESSAGE_SEND,
                                        Permission.MESSAGE_HISTORY
                                ),
                                Collections.singletonList(
                                        Permission.MESSAGE_ADD_REACTION
                                )
                        )
                        .complete();

                guildModel.setTicketSupportCategoryId(category.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: `" + category.getName() + "` wurde neu erstellt\n"
                        : ":star: `" + category.getName() + "` wurde initial erstellt\n";
                supportCategory = category;
            } else {
                log += ":white_check_mark: `" + categoryById.getName() + "` existiert\n";
                supportCategory = categoryById;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `\uD83C\uDFAB Support Tickets` konnte nicht erstellt werden\n";
        }

        try {
            Category categoryById = guildModel.getTicketArchiveCategoryId() != null
                    ? event.getGuild().getCategoryById(guildModel.getTicketArchiveCategoryId())
                    : null;
            if (categoryById == null) {
                boolean shouldExists = guildModel.getTicketArchiveCategoryId() != null;

                Category category = event.getGuild().createCategory("\uD83C\uDFAB Archiv")
                        .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(),
                                Arrays.asList(
                                        Permission.VIEW_CHANNEL,
                                        Permission.MESSAGE_HISTORY
                                ),
                                Arrays.asList(
                                        Permission.MESSAGE_SEND,
                                        Permission.MESSAGE_ADD_REACTION
                                )
                        )
                        .complete();

                guildModel.setTicketArchiveCategoryId(category.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: `" + category.getName() + "` wurde neu erstellt\n"
                        : ":star: `" + category.getName() + "` wurde initial erstellt\n";
            } else {
                log += ":white_check_mark: `" + categoryById.getName() + "` existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `\uD83C\uDFAB Archiv` konnte nicht erstellt werden\n";
        }

        // ---

        log += "\n**Textkanäle:**\n";
        TextChannel ticketTextChannel = null;
        try {
            TextChannel textChannelById = guildModel.getTicketCreateTextChannelId() != null
                    ? event.getGuild().getTextChannelById(guildModel.getTicketCreateTextChannelId())
                    : null;
            if (textChannelById == null) {
                boolean shouldExists = guildModel.getTicketCreateTextChannelId() != null;

                TextChannel textChannel = event.getGuild().createTextChannel("\uD83C\uDFAB-ticket-erstellen")
                        .setParent(supportCategory)
                        .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(),
                                Arrays.asList(
                                        Permission.MESSAGE_HISTORY,
                                        Permission.VIEW_CHANNEL
                                ),
                                Arrays.asList(
                                        Permission.MESSAGE_SEND,
                                        Permission.MESSAGE_ADD_REACTION
                                )
                        ).complete();

                guildModel.setTicketCreateTextChannelId(textChannel.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: `" + textChannel.getName() + "` wurde neu erstellt\n"
                        : ":star: `" + textChannel.getName() + "` wurde initial erstellt\n";
                ticketTextChannel = textChannel;
            } else {
                log += ":white_check_mark: `" + textChannelById.getName() + "` existiert\n";
                ticketTextChannel = textChannelById;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `\uD83C\uDFAB-ticket-erstellen` konnte nicht erstellt werden\n";
        }

        // ---

        log += "\n**Textnachrichten:**\n";

        try {
            Message messageById;

            try {
                messageById = guildModel.getTicketCreateTextMessageId() != null
                        ? Objects.requireNonNull(ticketTextChannel).retrieveMessageById(guildModel.getTicketCreateTextMessageId()).complete()
                        : null;
            } catch (Exception e) {
                messageById = null;
            }

            if (messageById == null) {
                boolean shouldExists = guildModel.getTicketCreateTextMessageId() != null;

                MessageEmbed messageEmbed = new EmbedBuilder()
                        .setTitle("ticket.ni.ls")
                        .setColor(0xff0000)
                        .setDescription(
                                "Um ein Ticket zu erstellen mit :envelope_with_arrow: reagieren"
                        )
                        .build();

                Message message = Objects.requireNonNull(ticketTextChannel).sendMessageEmbeds(messageEmbed).complete();
                message.addReaction(Emoji.fromUnicode("\uD83D\uDCE9")).complete();

                guildModel.setTicketCreateTextMessageId(message.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: :envelope_with_arrow: wurde neu erstellt\n"
                        : ":star: :envelope_with_arrow: wurde initial erstellt\n";
            } else if (messageById.getReactions().size() == 0) {
                messageById.addReaction(Emoji.fromUnicode("\uD83D\uDCE9")).complete();
                log += ":star: :envelope_with_arrow: Reaktion wurde hinzugefügt\n";
            } else {
                log += ":white_check_mark: :envelope_with_arrow: existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `envelope_with_arrow` konnte nicht erstellt werden\n";
        }

        // ---

        log += "\n**Log Textkanal:**\n";
        try {
            TextChannel textChannelById = guildModel.getTicketLogTextChannelId() != null
                    ? event.getGuild().getTextChannelById(guildModel.getTicketLogTextChannelId())
                    : null;
            if (textChannelById == null) {
                boolean shouldExists = guildModel.getTicketLogTextChannelId() != null;

                TextChannel textChannel = event.getGuild().createTextChannel("\uD83C\uDFAB-ticket-log")
                        .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(),
                                Collections.emptyList(),
                                Collections.singletonList(
                                        Permission.VIEW_CHANNEL
                                )
                        )
                        .addRolePermissionOverride(Long.parseLong(guildModel.getTicketSupportRoleId()),
                                Arrays.asList(
                                        Permission.VIEW_CHANNEL,
                                        Permission.MESSAGE_HISTORY
                                ),
                                Collections.emptyList()
                        )
                        .addRolePermissionOverride(Long.parseLong(guildModel.getTicketSupportPlusRoleId()),
                                Arrays.asList(
                                        Permission.VIEW_CHANNEL,
                                        Permission.MESSAGE_HISTORY
                                ),
                                Collections.emptyList()
                        )
                        .complete();

                guildModel.setTicketLogTextChannelId(textChannel.getId());

                log += shouldExists
                        ? ":arrows_counterclockwise: `" + textChannel.getName() + "` wurde neu erstellt\n"
                        : ":star: `" + textChannel.getName() + "` wurde initial erstellt\n";
            } else {
                log += ":white_check_mark: `" + textChannelById.getName() + "` existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `\uD83C\uDFAB-ticket-log` konnte nicht erstellt werden\n";
        }

        this.guildRepository.save(guildModel);

        event.getHook().sendMessage(log).queue();
    }
}
