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

package io.nilsdev.discordticketsupport.common.providers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import io.nilsdev.discordticketsupport.common.Constants;
import xyz.morphia.Morphia;
import xyz.morphia.ext.guice.GuiceExtension;

public class MorphiaProvider implements Provider<Morphia> {

    private final Injector injector;

    @Inject
    public MorphiaProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Morphia get() {
        Morphia morphia = new Morphia();

        new GuiceExtension(morphia, this.injector);

        morphia.mapPackage(Constants.COMMON_MODEL_PACKAGE);

        return morphia;
    }
}
