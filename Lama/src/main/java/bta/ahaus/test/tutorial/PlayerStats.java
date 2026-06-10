package bta.ahaus.test.tutorial;

public class PlayerStats {
    private static PlayerStats instance;
    private int xp = 0;

    private PlayerStats() {}

    public static PlayerStats getInstance() {
        if (instance == null) instance = new PlayerStats();
        return instance;
    }

    public void addXP(int amount) { xp += amount; }
    public int getXP()            { return xp; }
}