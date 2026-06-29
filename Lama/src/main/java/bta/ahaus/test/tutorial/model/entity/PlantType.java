package bta.ahaus.lamaDrama.model.entity;

import javafx.scene.paint.Color;

public enum PlantType {

    // ── Normale Feldfrüchte ──────────────────────────────────────────────
    KAROTTE   ("Karotte",    0, 20, Color.ORANGE,      "🥕", 1, 0),
    KARTOFFEL ("Kartoffel",  5, 30, Color.BURLYWOOD,   "🥔", 1, 0),
    WEIZEN    ("Weizen",     3, 15, Color.WHEAT,       "🌾", 1, 0),
    KOHL      ("Kohl",      10, 40, Color.LIGHTGREEN,  "🌿", 1, 0),
    TOMATE    ("Tomate",    15, 35, Color.TOMATO,      "🍅", 1, 0),
    KUERBIS   ("Kürbis",    25, 60, Color.DARKORANGE,  "🎃", 1, 0),
    

    // ── Spezialitäten aus dem Shop ──────────────────────────────────────
    APFEL      ("Apfel",       3, 0, Color.RED,       "🍎", 1, 30),
    SCHOKOLADE ("Schokolade",  5, 0, Color.BROWN,     "🍫", 1, 50),
    KRAEUTERMIX("Kräutermix",  8, 0, Color.DARKGREEN, "🌿", 1, 80),
    HONIG      ("Honig",      12, 0, Color.GOLDENROD, "🍯", 1, 100),
    NUGGET     ("Nuggie",      0, 0, Color.rgb(212,160,23), "🍗", 1, 0);
    

    public final String displayName;
    public final int seedCost;
    public final int growSeconds;
    public final Color color;
    public final String emoji;
    public final int harvestAmount;
    public final int minXP;

    PlantType(String displayName, int seedCost, int growSeconds,
              Color color, String emoji, int harvestAmount, int minXP) {
        this.displayName = displayName;
        this.seedCost = seedCost;
        this.growSeconds = growSeconds;
        this.color = color;
        this.emoji = emoji;
        this.harvestAmount = harvestAmount;
        this.minXP = minXP;
    }

    // Ist ein Shop-Item?
    public boolean isSpezialitaet() {
        return this == SCHOKOLADE
            || this == APFEL
            || this == KRAEUTERMIX
            || this == HONIG
            || this == NUGGET;
    }

    // Kann auf dem Feld angebaut werden?
    public boolean isPflanze() {
        return !isSpezialitaet();
    }
}