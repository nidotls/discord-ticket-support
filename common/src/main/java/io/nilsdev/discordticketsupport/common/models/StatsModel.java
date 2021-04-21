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
@Entity(value = "stats", noClassnameStored = true)
public class StatsModel extends Model {

    private long guilds;

    private long members;

    @PrePersist
    @SuppressWarnings("EmptyMethod")
    public void prePersist() {
        super.prePersist();
    }
}
