package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.di.AppModule;
import org.example.domain.IInventory;
import org.example.domain.INamed;
import org.example.domain.animal.*;
import org.example.domain.thing.Computer;
import org.example.domain.thing.Table;
import org.example.domain.thing.Thing;
import org.example.service.IZooService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        IZooService zoo = injector.getInstance(IZooService.class);

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        System.out.println("Moscow Zoo - console application");
        while (running) {
            System.out.println();
            System.out.println("Menu:");
            System.out.println("1) Add animal");
            System.out.println("2) Add thing");
            System.out.println("3) Report: animal count and total food/day");
            System.out.println("4) Animals eligible for petting zoo");
            System.out.println("5) Inventory report (name + number)");
            System.out.println("0) Exit");
            System.out.print("Select option: ");
            if (!sc.hasNextLine()) {
                System.out.println("\nInput is closed. Exiting.");
                break;
            }
            String cmd = sc.nextLine().trim();
            switch (cmd) {
                case "1":
                    addAnimalFlow(sc, zoo);
                    break;
                case "2":
                    addThingFlow(sc, zoo);
                    break;
                case "3":
                    reportAnimals(zoo);
                    break;
                case "4":
                    reportContactZoo(zoo);
                    break;
                case "5":
                    reportInventory(zoo);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void addAnimalFlow(Scanner sc, IZooService zoo) {
        System.out.println("Animal type: 1) Monkey 2) Rabbit 3) Tiger 4) Wolf");
        System.out.print("Your choice: ");
        if (!sc.hasNextLine()) {
            System.out.println("Input is closed. Cancelled.");
            return;
        }
        String type = sc.nextLine().trim();

        System.out.print("Animal name: ");
        if (!sc.hasNextLine()) {
            System.out.println("Input is closed. Cancelled.");
            return;
        }
        String name = sc.nextLine().trim();

        int number = readInt(sc, "Inventory number: ");
        int food = readInt(sc, "Food consumption (kg/day): ");

        boolean healthy = readBool(sc, "Healthy (y/n): ");

        Animal animal;
        switch (type) {
            case "1":
                int kindM = readKindness(sc);
                animal = new Monkey(name, number, food, healthy, kindM);
                break;
            case "2":
                int kindR = readKindness(sc);
                animal = new Rabbit(name, number, food, healthy, kindR);
                break;
            case "3":
                animal = new Tiger(name, number, food, healthy);
                break;
            case "4":
                animal = new Wolf(name, number, food, healthy);
                break;
            default:
                System.out.println("Unknown type");
                return;
        }

        boolean accepted = zoo.addAnimal(animal);
        System.out.println(accepted ? "Animal accepted to the zoo" : "Animal rejected by the veterinary clinic");
    }

    private static int readKindness(Scanner sc) {
        while (true) {
            int k = readInt(sc, "Kindness level (0..10): ");
            if (k >= 0 && k <= 10) return k;
            System.out.println("Invalid input, try again.");
        }
    }

    private static void addThingFlow(Scanner sc, IZooService zoo) {
        System.out.println("Thing type: 1) Table 2) Computer");
        System.out.print("Your choice: ");
        if (!sc.hasNextLine()) {
            System.out.println("Input is closed. Cancelled.");
            return;
        }
        String type = sc.nextLine().trim();

        System.out.print("Name: ");
        if (!sc.hasNextLine()) {
            System.out.println("Input is closed. Cancelled.");
            return;
        }
        String name = sc.nextLine().trim();
        int number = readInt(sc, "Inventory number: ");

        Thing thing;
        switch (type) {
            case "1":
                thing = new Table(name, number);
                break;
            case "2":
                thing = new Computer(name, number);
                break;
            default:
                System.out.println("Unknown type");
                return;
        }
        zoo.addThing(thing);
        System.out.println("Thing added to inventory");
    }

    private static void reportAnimals(IZooService zoo) {
        int count = zoo.getAnimals().size();
        int totalFood = zoo.getTotalFoodKgPerDay();
        System.out.println("Animals in zoo: " + count);
        System.out.println("Total food consumption (kg/day): " + totalFood);
    }

    private static void reportContactZoo(IZooService zoo) {
        List<Herbo> list = zoo.getContactAnimals();
        if (list.isEmpty()) {
            System.out.println("No eligible animals");
            return;
        }
        System.out.println("Animals for petting zoo:");
        for (Herbo h : list) {
            System.out.println("- " + h.getName() + " (#" + h.getNumber() + ", kindness=" + h.getKindness() + ")");
        }
    }

    private static void reportInventory(IZooService zoo) {
        List<IInventory> inv = zoo.getAllInventory();
        if (inv.isEmpty()) {
            System.out.println("Inventory is empty");
            return;
        }
        System.out.println("Inventory (type - name - number):");
        for (IInventory item : inv) {
            String name = (item instanceof INamed) ? ((INamed) item).getName() : item.getClass().getSimpleName();
            String type;
            if (item instanceof Animal) {
                if (item instanceof Herbo) type = "Herbivore";
                else if (item instanceof Predator) type = "Predator";
                else type = "Animal";
            } else if (item instanceof Thing) {
                type = "Thing";
            } else {
                type = item.getClass().getSimpleName();
            }
            System.out.println("- [" + type + "] " + name + " - #" + item.getNumber());
        }
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) {
                System.out.println("Input is closed. Exiting.");
                System.exit(0);
            }
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Enter an integer");
            }
        }
    }

    private static boolean readBool(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) {
                System.out.println("Input is closed. Exiting.");
                System.exit(0);
            }
            String s = sc.nextLine().trim().toLowerCase();
            if (s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            System.out.println("Please answer y/n");
        }
    }
}