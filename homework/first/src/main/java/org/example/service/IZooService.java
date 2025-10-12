package org.example.service;

import org.example.domain.animal.Animal;
import org.example.domain.animal.Herbo;
import org.example.domain.thing.Thing;
import org.example.domain.IInventory;

import java.util.List;

public interface IZooService {
    boolean addAnimal(Animal animal); // приемка через ветклинику
    void addThing(Thing thing);

    List<Animal> getAnimals();
    List<Herbo> getContactAnimals();
    int getTotalFoodKgPerDay();

    List<IInventory> getAllInventory();
}

