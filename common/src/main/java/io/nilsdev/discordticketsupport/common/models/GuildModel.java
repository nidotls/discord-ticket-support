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

package io.nilsdev.discordticketsupport.common.models;

import dev.morphia.annotations.*;
import io.nilsdev.discordticketsupport.common.domain.Model;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@Entity(value = "guilds", useDiscriminator = false)
@Indexes({
        @Index(options = @IndexOptions(unique = true), fields = {
                @Field("guildId")
        })
})
public class GuildModel extends Model {

    private String guildId;

    // Categories

    private String ticketSupportCategoryId;

    private String ticketArchiveCategoryId;

    // Channel

    private String ticketCreateTextChannelId;

    private String ticketCreateTextMessageId;

    // Roles

    private String ticketSupportRoleId;

    private String ticketSupportPlusRoleId;

    private String ticketSupportBanRoleId;

    // Log

    private String ticketLogTextChannelId;

    @PrePersist
    @SuppressWarnings("EmptyMethod")
    public void prePersist() {
        super.prePersist();
    }
}
