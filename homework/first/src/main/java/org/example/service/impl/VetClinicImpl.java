package org.example.service.impl;

import com.google.inject.Singleton;
import org.example.domain.animal.Animal;
import org.example.service.IVetClinic;

@Singleton
public class VetClinicImpl implements IVetClinic {
    @Override
    public boolean isHealthy(Animal animal) {
        // Простая политика: доверяем внутреннему флагу здоровья животного
        return animal.isHealthy();
    }
}

