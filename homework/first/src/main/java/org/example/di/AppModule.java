package org.example.di;

import com.google.inject.AbstractModule;
import org.example.service.IVetClinic;
import org.example.service.IZooService;
import org.example.service.impl.VetClinicImpl;
import org.example.service.impl.ZooServiceImpl;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IVetClinic.class).to(VetClinicImpl.class);
        bind(IZooService.class).to(ZooServiceImpl.class);
    }
}

