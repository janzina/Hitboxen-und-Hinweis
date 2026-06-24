package bta.ahaus.test.tutorial;

import java.util.EnumMap;
import java.util.Map;

public class Inventory {

    private int coins = 50;

    // Saatgut
    private final Map<PlantType, Integer> seeds =
            new EnumMap<>(PlantType.class);

    // Ernte
    private final Map<PlantType, Integer> harvest =
            new EnumMap<>(PlantType.class);

    public int getCoins() {
        return coins;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public boolean spend(int amount) {
        if (coins < amount)
            return false;

        coins -= amount;
        return true;
    }

    // -----------------------
    // Saatgut
    // -----------------------

    public void addSeed(PlantType type, int amount) {
        seeds.merge(type, amount, Integer::sum);
    }

    public int getSeedCount(PlantType type) {
        return seeds.getOrDefault(type, 0);
    }

    public boolean removeSeed(PlantType type, int amount) {

        int current = getSeedCount(type);

        if (current < amount)
            return false;

        seeds.put(type, current - amount);
        return true;
    }

    // -----------------------
    // Ernte
    // -----------------------

    public void addHarvest(PlantType type, int amount) {
        harvest.merge(type, amount, Integer::sum);
    }

    public int getHarvestCount(PlantType type) {
        return harvest.getOrDefault(type, 0);
    }

    public boolean removeHarvest(PlantType type, int amount) {

        int current = getHarvestCount(type);

        if (current < amount)
            return false;

        harvest.put(type, current - amount);
        return true;
    }
}