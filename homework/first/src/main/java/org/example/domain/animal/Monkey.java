package org.example.domain.animal;

/**
 * Обезьяна (для простоты считаем травоядной с параметром доброты).
 */
public class Monkey extends Herbo {
    public Monkey(String name, int number, int foodKgPerDay, boolean healthy, int kindness) {
        super(name, number, foodKgPerDay, healthy, kindness);
    }
}

