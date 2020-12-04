package io.nilsdev.discordticketsupport.bot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.entities.CommandEvent;
import io.nilsdev.discordticketsupport.bot.Bot;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import io.nilsdev.discordticketsupport.common.repositories.GuildRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.util.Optional;

@CommandController
public class UninstallCommand {

    @Command("uninstall")
    public void onCommand(CommandEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply(event.getAuthor().getAsMention() + ", der Befehl install erfordert, dass du die Berechtigung \"Administrator\" hast.");
            return;
        }

        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.ADMINISTRATOR)) {
            event.reply(event.getAuthor().getAsMention() + ", ich brauche die Berechtigung \"Administrator\", damit der Befehl \\`install\\` funktioniert.`");
            return;
        }

        GuildRepository guildRepository = Bot.getInjector().getInstance(GuildRepository.class);

        GuildModel guildModel = guildRepository.findByGuildId(event.getGuild().getId());

        if (guildModel == null) {
            guildModel = GuildModel.builder()
                    .guildId(event.getGuild().getId())
                    .build();
            guildRepository.save(guildModel);
        }

        // ---

        if (guildModel == null) {
            event.reply("Ich bin auf diesem Discord-Server nicht installiert.");
            return;
        }

        // ---

        String log = "**Rollen:**\n";

        try {
            Role roleById = guildModel.getTicketSupportRoleId() != null
                    ? event.getGuild().getRoleById(guildModel.getTicketSupportRoleId())
                    : null;
            if (guildModel.getTicketSupportRoleId() != null || roleById != null) {
                boolean shouldExists = roleById != null;

                if (shouldExists) {
                    roleById.delete().complete();
                }

                guildModel.setTicketSupportRoleId(null);

                log += shouldExists
                        ? ":no_entry_sign: `" + roleById.getName() + "` wurde entfernt\n"
                        : ":o: `ticketSupportRoleId` wurde aus der DB entfernt\n";
            } else {
                log += ":x: `ticketSupportRoleId` hat nicht existiert\n";
            }
        } catch (HierarchyException e) {
            log += ":bug: `ticketSupportRoleId` konnte nicht entfernt werden (Rolle höher als Bot Rolle)\n";
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `ticketSupportRoleId` konnte nicht entfernt werden\n";
        }

        try {
            Role roleById = guildModel.getTicketSupportPlusRoleId() != null
                    ? event.getGuild().getRoleById(guildModel.getTicketSupportPlusRoleId())
                    : null;
            if (guildModel.getTicketSupportPlusRoleId() != null || roleById != null) {
                boolean shouldExists = roleById != null;

                if (shouldExists) {
                    roleById.delete().complete();
                }

                guildModel.setTicketSupportPlusRoleId(null);

                log += shouldExists
                        ? ":no_entry_sign: `" + roleById.getName() + "` wurde entfernt\n"
                        : ":o: `ticketSupportPlusRoleId` wurde aus der DB entfernt\n";
            } else {
                log += ":x: `ticketSupportPlusRoleId` hat nicht existiert\n";
            }
        } catch (HierarchyException e) {
            log += ":bug: `ticketSupportRoleId` konnte nicht entfernt werden (Rolle höher als Bot Rolle)\n";
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `ticketSupportPlusRoleId` konnte nicht entfernt werden\n";
        }

        try {
            Role roleById = guildModel.getTicketSupportBanRoleId() != null
                    ? event.getGuild().getRoleById(guildModel.getTicketSupportBanRoleId())
                    : null;
            if (guildModel.getTicketSupportBanRoleId() != null || roleById != null) {
                boolean shouldExists = roleById != null;

                if (shouldExists) {
                    roleById.delete().complete();
                }

                guildModel.setTicketSupportBanRoleId(null);

                log += shouldExists
                        ? ":no_entry_sign: `" + roleById.getName() + "` wurde entfernt\n"
                        : ":o: `ticketSupportBanRoleId` wurde aus der DB entfernt\n";
            } else {
                log += ":x: `ticketSupportBanRoleId` hat nicht existiert\n";
            }
        } catch (HierarchyException e) {
            log += ":bug: `ticketSupportRoleId` konnte nicht entfernt werden (Rolle höher als Bot Rolle)\n";
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `ticketSupportBanRoleId` konnte nicht entfernt werden\n";
        }

        // ---

        log += "\n**Kategorien:**\n";
        try {
            Category categoryById = guildModel.getTicketSupportCategoryId() != null
                    ? event.getGuild().getCategoryById(guildModel.getTicketSupportCategoryId())
                    : null;
            if (guildModel.getTicketSupportCategoryId() != null || categoryById != null) {
                boolean shouldExists = categoryById != null;

                if (shouldExists) {
                    for (GuildChannel channel : categoryById.getChannels()) {
                        if (channel.getId().equals(guildModel.getTicketCreateTextChannelId())) continue;

                        channel.delete().complete();
                    }
                    categoryById.delete().complete();
                }

                guildModel.setTicketSupportCategoryId(null);

                log += shouldExists
                        ? ":no_entry_sign: `" + categoryById.getName() + "` wurde entfernt\n"
                        : ":o: `ticketSupportCategoryId` wurde aus der DB entfernt\n";
            } else {
                log += ":x: `ticketSupportCategoryId` hat nicht existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `ticketSupportCategoryId` konnte nicht entfernt werden\n";
        }

        try {
            Category categoryById = guildModel.getTicketArchiveCategoryId() != null
                    ? event.getGuild().getCategoryById(guildModel.getTicketArchiveCategoryId())
                    : null;
            if (guildModel.getTicketArchiveCategoryId() != null || categoryById != null) {

                boolean shouldExists = categoryById != null;

                if (shouldExists) {
                    for (GuildChannel channel : categoryById.getChannels()) {
                        channel.delete().complete();
                    }
                    categoryById.delete().complete();
                }

                guildModel.setTicketArchiveCategoryId(null);

                log += shouldExists
                        ? ":no_entry_sign: `" + categoryById.getName() + "` wurde entfernt\n"
                        : ":o: `ticketArchiveCategoryId` wurde aus der DB entfernt\n";
            } else {
                log += ":x: `ticketArchiveCategoryId` hat nicht existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `ticketArchiveCategoryId` konnte nicht entfernt werden\n";
        }

        // ---

        log += "\n**Textkanäle:**\n";
        try {
            TextChannel textChannelById = guildModel.getTicketCreateTextChannelId() != null
                    ? event.getGuild().getTextChannelById(guildModel.getTicketCreateTextChannelId())
                    : null;
            if (guildModel.getTicketCreateTextChannelId() != null || textChannelById != null) {
                boolean shouldExists = textChannelById != null;

                if (shouldExists) {
                    textChannelById.delete().complete();
                }

                guildModel.setTicketCreateTextChannelId(null);

                log += shouldExists
                        ? ":no_entry_sign: `" + textChannelById.getName() + "` wurde entfernt\n"
                        : ":o: `ticketCreateTextChannelId` wurde aus der DB entfernt\n";
            } else {
                log += ":x: `ticketCreateTextChannelId` hat nicht existiert\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log += ":bug: `ticketCreateTextChannelId` konnte nicht entfernt werden\n";
        }

        // ---

        guildModel.setTicketCreateTextMessageId(null);

        guildRepository.save(guildModel);

        try {
            event.reply(log);
        } catch (Exception e) {
            Optional<TextChannel> optionalGuildChannel = event.getGuild().getTextChannels().stream().findFirst();

            if(!optionalGuildChannel.isPresent()) return;

            optionalGuildChannel.get().sendMessage(log).queue();
        }
    }
}
