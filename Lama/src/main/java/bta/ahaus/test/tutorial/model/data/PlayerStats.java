package bta.ahaus.lamaDrama.model.data;

import java.util.ArrayList;
import java.util.List;

public class PlayerStats {
    private static final PlayerStats INSTANCE = new PlayerStats();
    private PlayerStats() {}
    public static PlayerStats getInstance() { return INSTANCE; }
    public int getXp() { return xp;}
    public int getMuenzen() {return coins;}
    public void setXp(int xp) {this.xp = xp;}
    public void setMuenzen(int muenzen) {this.coins = muenzen;}
    public void setHunger(int hunger) {this.hunger = hunger;}
    

    // ── Hunger ────────────────────────────────────────────────────────────────
    private int hunger = 100;
    public int getHunger()                { return hunger; }
    public void    addHunger(int amount)   { hunger = Math.min(100, hunger + amount); }
    public void    removeHunger(int amount){ hunger = Math.max(0,   hunger - amount); }
    public boolean isFull()                   { return hunger >= 100; }

    // ── XP ────────────────────────────────────────────────────────────────────
    public static final int BLACK_HOLE_XP_THRESHOLD = 500; // <- hier anpassen

    private int xp = 0;
    private boolean blackHoleTriggered = false;
    private final List<Runnable> blackHoleListeners = new ArrayList<>();

    public int  getXP() { return xp; }

    public void addXP(int amount) {
        xp += amount;
        if (!blackHoleTriggered && xp >= BLACK_HOLE_XP_THRESHOLD) {
            blackHoleTriggered = true;
            blackHoleListeners.forEach(Runnable::run);
        }
    }

    /** Wird einmalig gefeuert sobald XP-Schwelle erreicht ist. */
    public void addBlackHoleListener(Runnable listener) {
        blackHoleListeners.add(listener);
    }

    public boolean isBlackHoleTriggered() { return blackHoleTriggered; }

    // ── Coins ─────────────────────────────────────────────────────────────────
    private int coins = 0;
    public int  getCoins()           { return coins; }
    public void addCoins(int amount) { coins += amount; }
    
    public void resetXP() {
    xp = 0;
    blackHoleTriggered = false;
}

}
