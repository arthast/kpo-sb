package org.example.domain.animal;

/**
 * Травоядное животное, имеет показатель доброты 0..10.
 */
public abstract class Herbo extends Animal {
    private final int kindness; // 0..10

    protected Herbo(String name, int number, int foodKgPerDay, boolean healthy, int kindness) {
        super(name, number, foodKgPerDay, healthy);
        if (kindness < 0 || kindness > 10) {
            throw new IllegalArgumentException("Kindness must be between 0 and 10");
        }
        this.kindness = kindness;
    }

    public int getKindness() {
        return kindness;
    }

    public boolean isPettingAllowed() {
        return kindness > 5;
    }
}

