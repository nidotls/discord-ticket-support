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
