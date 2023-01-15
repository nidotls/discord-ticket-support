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
