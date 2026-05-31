package bta.ahaus.test.tutorial;

import javafx.scene.paint.Color;

/**
 * Alle anpflanzbaren Pflanzenarten mit Preis, Wachstumszeit und Farbe.
 */
public enum PlantType {

    KAROTTE   ("Karotte",    0,   20, Color.ORANGE,      "🥕", 1),
    KARTOFFEL ("Kartoffel",  5,   30, Color.BURLYWOOD,   "🥔", 2),
    KOHL      ("Kohl",      10,   40, Color.LIGHTGREEN,  "🌿", 3),
    TOMATE    ("Tomate",    15,   35, Color.TOMATO,      "🍅", 4),
    KUERBIS   ("Kürbis",   25,   60, Color.DARKORANGE,  "🎃", 8),
    WEIZEN    ("Weizen",    3,   15, Color.WHEAT,       "🌾", 1);

    public final String  displayName;
    /** Preis zum Kaufen des Saatguts (Münzen) */
    public final int     seedCost;
    /** Gesamtwachstumszeit in Sekunden */
    public final int     growSeconds;
    public final Color   color;
    public final String  emoji;
    /** Ertrag beim Ernten (Anzahl Items ins Inventar) */
    public final int     harvestAmount;

    PlantType(String displayName, int seedCost, int growSeconds,
              Color color, String emoji, int harvestAmount) {
        this.displayName   = displayName;
        this.seedCost      = seedCost;
        this.growSeconds   = growSeconds;
        this.color         = color;
        this.emoji         = emoji;
        this.harvestAmount = harvestAmount;
    }
}