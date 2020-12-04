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

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

import java.util.List;

public abstract class Repository<ValueType> {

    private final Class<ValueType> entityClazz;

    private final Datastore datastore;

    protected Repository(Class<ValueType> entityClazz, Datastore datastore) {
        this.entityClazz = entityClazz;
        this.datastore = datastore;
    }

    protected Datastore getDatastore() {
        return this.datastore;
    }

    public ValueType save(ValueType value) {
        this.datastore.save(value);
        return value;
    }

    public boolean deleteById(ObjectId id) {
        WriteResult writeResult = this.datastore.delete(this.entityClazz, id);
        return writeResult.getN() == 1;
    }

    public ValueType findById(ObjectId id) {
        return this.datastore.get(this.entityClazz, id);
    }

    public List<ValueType> findAll() {
        return this.createQuery().asList();
    }

    protected Query<ValueType> createQuery() {
        return this.datastore.createQuery(this.entityClazz);
    }
}
