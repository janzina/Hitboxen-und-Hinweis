package bta.ahaus.test.tutorial;

public class LamaDreck {

    // ← Hier anpassen wie schnell das Lama dreckig wird (in Sekunden)
    private static final double DRECK_ZEIT = 5 * 60; // 5 Minuten

    private double dreckLevel = 0.0;  // 0.0 = sauber, 1.0 = sehr dreckig
    private double timer      = 0.0;

    // Wird jeden Frame aufgerufen
    public void update(double tpf) {
        if (dreckLevel < 1.0) {
            timer     += tpf;
            dreckLevel = Math.min(1.0, timer / DRECK_ZEIT);
        }
    }

    // Nach dem Putzen aufrufen
    public void reset() {
        dreckLevel = 0.0;
        timer      = 0.0;
    }

    // 0-100
    public int getDreckProzent() {
        return (int)(dreckLevel * 100);
    }

    // true = Lama ist dreckig genug zum Putzen
    public boolean kannGeputztWerden() {
        return dreckLevel >= 1.0;
    }
}