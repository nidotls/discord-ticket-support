package io.nilsdev.discordticketsupport.common.repositories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.nilsdev.discordticketsupport.common.Constants;
import io.nilsdev.discordticketsupport.common.domain.Repository;
import io.nilsdev.discordticketsupport.common.models.GuildModel;
import xyz.morphia.Datastore;

@Singleton
public class GuildRepository extends Repository<GuildModel> {

    @Inject
    protected GuildRepository(@Named(Constants.DATASTORE) Datastore datastore) {
        super(GuildModel.class, datastore);
    }

    public GuildModel findByGuildId(String guildId) {
        return this.createQuery()
                .field("guildId").equal(guildId)
                .get();
    }
}
