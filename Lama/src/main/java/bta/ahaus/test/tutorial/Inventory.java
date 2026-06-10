package bta.ahaus.test.tutorial;

import java.util.EnumMap;
import java.util.Map;

public class Inventory {

    private int coins = 50;
    private final Map<PlantType, Integer> items =
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

    public void addItem(PlantType type, int count) {
        items.merge(type, count, Integer::sum);
    }

    public int getItem(PlantType type) {
        return items.getOrDefault(type, 0);
    }

    public boolean removeItem(PlantType type, int count) {

        int current = getItem(type);

        if (current < count)
            return false;

        items.put(type, current - count);
        return true;
    }

    public Map<PlantType, Integer> getAllItems() {
        return Map.copyOf(items);
    }
}