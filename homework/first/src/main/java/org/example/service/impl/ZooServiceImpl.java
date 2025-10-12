package org.example.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.example.domain.IInventory;
import org.example.domain.animal.Animal;
import org.example.domain.animal.Herbo;
import org.example.domain.thing.Thing;
import org.example.service.IVetClinic;
import org.example.service.IZooService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class ZooServiceImpl implements IZooService {
    private final IVetClinic vetClinic;
    private final List<Animal> animals = new ArrayList<>();
    private final List<Thing> things = new ArrayList<>();

    @Inject
    public ZooServiceImpl(IVetClinic vetClinic) {
        this.vetClinic = vetClinic;
    }

    @Override
    public boolean addAnimal(Animal animal) {
        if (vetClinic.isHealthy(animal)) {
            animals.add(animal);
            return true;
        }
        return false;
    }

    @Override
    public void addThing(Thing thing) {
        things.add(thing);
    }

    @Override
    public List<Animal> getAnimals() {
        return Collections.unmodifiableList(animals);
    }

    @Override
    public List<Herbo> getContactAnimals() {
        return animals.stream()
                .filter(a -> a instanceof Herbo)
                .map(a -> (Herbo) a)
                .filter(Herbo::isPettingAllowed)
                .collect(Collectors.toList());
    }

    @Override
    public int getTotalFoodKgPerDay() {
        return animals.stream().mapToInt(Animal::getFoodKgPerDay).sum();
    }

    @Override
    public List<IInventory> getAllInventory() {
        return Stream.concat(animals.stream().map(a -> (IInventory) a), things.stream())
                .collect(Collectors.toList());
    }
}

