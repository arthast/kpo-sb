package org.example.service;

import org.example.domain.animal.Tiger;
import org.example.service.impl.VetClinicImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VetClinicImplTest {
    @Test
    void isHealthy_returnsAnimalFlag() {
        VetClinicImpl clinic = new VetClinicImpl();
        Tiger healthy = new Tiger("T1", 1, 5, true);
        Tiger ill = new Tiger("T2", 2, 6, false);
        assertTrue(clinic.isHealthy(healthy));
        assertFalse(clinic.isHealthy(ill));
    }
}

