package io.nilsdev.discordticketsupport.common.domain;

import lombok.*;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.PrePersist;

import java.util.Date;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Model {

    @Id
    private final ObjectId id;

    private Date updatedAt;
    private Date createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt != null ? this.createdAt : new Date();
        this.updatedAt = new Date();
    }
}
