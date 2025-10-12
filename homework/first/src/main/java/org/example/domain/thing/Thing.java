package org.example.domain.thing;

import org.example.domain.IInventory;
import org.example.domain.INamed;

/**
 * Базовая инвентаризируемая вещь.
 */
public abstract class Thing implements IInventory, INamed {
    private final String name;
    private final int number;

    protected Thing(String name, int number) {
        this.name = name;
        this.number = number;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "name='" + name + '\'' +
            ", number=" + number +
            '}';
    }
}

