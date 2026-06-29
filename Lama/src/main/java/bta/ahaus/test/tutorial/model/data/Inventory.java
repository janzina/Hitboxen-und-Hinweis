package bta.ahaus.lamaDrama.model.data;

import bta.ahaus.lamaDrama.model.entity.PlantType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private int coins = 50;

    // Saatgut
    private final Map<PlantType, Integer> seeds =
            new EnumMap<>(PlantType.class);

    // Ernte
    private final Map<PlantType, Integer> items =
            new EnumMap<>(PlantType.class);

    // -----------------------
    // Coins
    // -----------------------

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public void addCoins(int amount) { coins += amount; }

    public boolean spend(int amount) {
        if (coins < amount) return false;
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
        if (current < amount) return false;
        seeds.put(type, current - amount);
        return true;
    }

    public Map<PlantType, Integer> getAllSeeds() {
        return new HashMap<>(seeds);
    }

    // -----------------------
    // Ernte
    // -----------------------

    public void addItem(PlantType type, int count) {
        items.merge(type, count, Integer::sum);
    }

    public int getItem(PlantType type) {
        return items.getOrDefault(type, 0);
    }

    public boolean removeItem(PlantType type, int count) {
        int current = getItem(type);
        if (current < count) return false;
        items.put(type, current - count);
        return true;
    }

    public Map<PlantType, Integer> getAllItems() {
        return new HashMap<>(items);
    }
}