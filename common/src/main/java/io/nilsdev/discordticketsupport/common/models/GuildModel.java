package io.nilsdev.discordticketsupport.common.models;

import io.nilsdev.discordticketsupport.common.domain.Model;
import lombok.*;
import xyz.morphia.annotations.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@Entity(value = "guilds", noClassnameStored = true)
@Indexes({
        @Index(options = @IndexOptions(unique = true), fields = {
                @Field("guildId")
        })
})
public class GuildModel extends Model {

    private String guildId;

    private String ticketArchiveCategoryId;

    private String ticketCreateTextChannelId;

    private String ticketCreateTextMessageId;

    private String ticketSupportBanRoleId;

    private String ticketSupportCategoryId;

    private String ticketSupportPlusRoleId;

    private String ticketSupportRoleId;

    @PrePersist
    @SuppressWarnings("EmptyMethod")
    public void prePersist() {
        super.prePersist();
    }
}
