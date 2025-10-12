package org.example.service;

import org.example.domain.animal.Animal;

/** Ветклиника проверяет состояние здоровья животных. */
public interface IVetClinic {
    boolean isHealthy(Animal animal);
}

