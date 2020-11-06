package io.nilsdev.ticketsupport.common.providers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import io.nilsdev.ticketsupport.common.Constants;
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
