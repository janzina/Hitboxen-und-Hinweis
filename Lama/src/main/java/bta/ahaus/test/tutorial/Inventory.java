package bta.ahaus.test.tutorial;

import java.util.EnumMap;
import java.util.Map;

/**
 * Spieler-Inventar: speichert geerntete Pflanzen und Münzen.
 */
public class Inventory {

    private int coins = 50; // Startkapital
    private final Map<PlantType, Integer> items = new EnumMap<>(PlantType.class);

    // ── Münzen ────────────────────────────────────────────────────────────────

    public int getCoins() { return coins; }

    /** Gibt true zurück, wenn der Kauf erfolgreich war. */
    public boolean spend(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public void addCoins(int amount) { coins += amount; }

    // ── Items ─────────────────────────────────────────────────────────────────

    public void addItem(PlantType type, int count) {
        items.merge(type, count, Integer::sum);
    }

    public int getItem(PlantType type) {
        return items.getOrDefault(type, 0);
    }

    public Map<PlantType, Integer> getAllItems() {
        return Map.copyOf(items);
    }
}