package bta.ahaus.test.tutorial;

public class PlayerStats {
    private static final PlayerStats INSTANCE = new PlayerStats();
    private PlayerStats() {}
    public static PlayerStats getInstance() { return INSTANCE; }

    // ── Hunger ────────────────────────────────────────────────────────────────
    private double hunger = 100;

    public double  getHunger()                { return hunger; }
    public void    addHunger(double amount)   { hunger = Math.min(100, hunger + amount); }
    public void    removeHunger(double amount){ hunger = Math.max(0,   hunger - amount); }
    public boolean isFull()                   { return hunger >= 100; }

    // ── XP ────────────────────────────────────────────────────────────────────
    private int xp = 0;

    public int  getXP()           { return xp; }
    public void addXP(int amount) { xp += amount; }

    // ── Coins ─────────────────────────────────────────────────────────────────
    private int coins = 0;

    public int  getCoins()           { return coins; }
    public void addCoins(int amount) { coins += amount; }
}