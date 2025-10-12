package org.example.domain.animal;

/**
 * Хищник.
 */
public abstract class Predator extends Animal {
    protected Predator(String name, int number, int foodKgPerDay, boolean healthy) {
        super(name, number, foodKgPerDay, healthy);
    }
}

