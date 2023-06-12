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

package io.nilsdev.discordticketsupport.common.domain;

import com.mongodb.client.result.DeleteResult;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Repository<T> {

    private final Class<T> entityClazz;

    private final Datastore datastore;

    protected Repository(Class<T> entityClazz, Datastore datastore) {
        this.entityClazz = entityClazz;
        this.datastore = datastore;
    }

    protected Datastore getDatastore() {
        return this.datastore;
    }

    public T save(T value) {
        this.datastore.save(value);
        return value;
    }

    public boolean deleteById(ObjectId id) {
        DeleteResult result = this.datastore.delete(findById(id));
        return result.getDeletedCount() > 0;
    }

    public T findById(ObjectId id) {
        return this.createQuery().filter(Filters.eq("_id", id)).first();
    }

    public List<T> findAll() {
        return this.createQuery().stream().collect(Collectors.toList());
    }

    protected Query<T> createQuery() {
        return this.datastore.find(this.entityClazz);
    }
}
