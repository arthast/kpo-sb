package org.example.service;

import org.example.domain.animal.Monkey;
import org.example.domain.animal.Rabbit;
import org.example.domain.animal.Tiger;
import org.example.domain.animal.Wolf;
import org.example.domain.thing.Computer;
import org.example.domain.thing.Table;
import org.example.service.impl.ZooServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ZooServiceImplTest {
    private IVetClinic vetClinic;
    private IZooService zoo;

    @BeforeEach
    void setUp() {
        vetClinic = Mockito.mock(IVetClinic.class);
        zoo = new ZooServiceImpl(vetClinic);
    }

    @Test
    void addAnimal_acceptsOnlyHealthy() {
        Monkey healthy = new Monkey("Momo", 101, 3, true, 8);
        Tiger ill = new Tiger("Tig", 102, 7, false);

        when(vetClinic.isHealthy(healthy)).thenReturn(true);
        when(vetClinic.isHealthy(ill)).thenReturn(false);

        assertTrue(zoo.addAnimal(healthy));
        assertFalse(zoo.addAnimal(ill));
        assertEquals(1, zoo.getAnimals().size());
    }

    @Test
    void totalFoodKgPerDay_sumsUp() {
        Monkey a = new Monkey("A", 1, 2, true, 6);
        Rabbit b = new Rabbit("B", 2, 1, true, 4);
        when(vetClinic.isHealthy(any())).thenReturn(true);

        zoo.addAnimal(a);
        zoo.addAnimal(b);
        assertEquals(3, zoo.getTotalFoodKgPerDay());
    }

    @Test
    void contactAnimals_filtersHerbivoresByKindness() {
        Monkey kind = new Monkey("Kind", 1, 1, true, 7);
        Rabbit notKind = new Rabbit("Shy", 2, 1, true, 5); // 5 -> не попадает
        Wolf predator = new Wolf("Wolfy", 3, 4, true);
        when(vetClinic.isHealthy(any())).thenReturn(true);

        zoo.addAnimal(kind);
        zoo.addAnimal(notKind);
        zoo.addAnimal(predator);

        List<?> contact = zoo.getContactAnimals();
        assertEquals(1, contact.size());
        assertEquals("Kind", ((org.example.domain.animal.Herbo) contact.get(0)).getName());
    }

    @Test
    void inventory_containsAnimalsAndThings() {
        when(vetClinic.isHealthy(any())).thenReturn(true);
        zoo.addAnimal(new Monkey("M", 11, 1, true, 9));
        zoo.addThing(new Table("T1", 200));
        zoo.addThing(new Computer("PC", 201));

        var inv = zoo.getAllInventory();
        assertEquals(3, inv.size());
        assertTrue(inv.stream().anyMatch(i -> i.getNumber() == 11));
        assertTrue(inv.stream().anyMatch(i -> i.getNumber() == 200));
        assertTrue(inv.stream().anyMatch(i -> i.getNumber() == 201));
    }
}

