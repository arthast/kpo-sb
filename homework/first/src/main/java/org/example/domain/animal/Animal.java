package org.example.domain.animal;

import org.example.domain.IAlive;
import org.example.domain.IInventory;
import org.example.domain.INamed;

/**
 * Базовый класс животного.
 */
public abstract class Animal implements IAlive, IInventory, INamed {
    private final String name;
    private final int number;
    private final int foodKgPerDay;
    private final boolean healthy;

    protected Animal(String name, int number, int foodKgPerDay, boolean healthy) {
        this.name = name;
        this.number = number;
        this.foodKgPerDay = foodKgPerDay;
        this.healthy = healthy;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getFoodKgPerDay() {
        return foodKgPerDay;
    }

    public boolean isHealthy() {
        return healthy;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "name='" + name + '\'' +
            ", number=" + number +
            ", foodKgPerDay=" + foodKgPerDay +
            ", healthy=" + healthy +
            '}';
    }
}
