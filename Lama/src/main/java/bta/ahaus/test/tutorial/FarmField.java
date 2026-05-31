package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import java.util.HashMap;
import java.util.Map;

/**
 * Das Ackerstück: ein COLS×ROWS-Gitter.
 *
 * zIndex-Struktur:
 *   Map Layer 0/1/2  → Map-Tiles
 *   FarmField Boden  → zIndex 5   (über allen Map-Layern, sichtbar)
 *   FarmField Rahmen → zIndex 6
 *   Pflanzen         → zIndex 7
 *   Gebäude          → zIndex 50
 *   Spieler          → zIndex 100
 */
public class FarmField {

    public static final int CELL_W = 64;
    public static final int CELL_H = 64;
    public static final int COLS   = 5;
    public static final int ROWS   = 4;

    private final double worldX;
    private final double worldY;

    private final Map<String, Entity> plants = new HashMap<>();

    public FarmField(double worldX, double worldY) {
        this.worldX = worldX;
        this.worldY = worldY;
        buildGrid();
    }

    // ── Gitter aufbauen ───────────────────────────────────────────────────────

    private void buildGrid() {

        // 1) Großer Erd-Hintergrund für das gesamte Feld
        Rectangle fieldBg = new Rectangle(COLS * CELL_W, ROWS * CELL_H);
        fieldBg.setFill(Color.rgb(101, 67, 33, 0.75));       // Erde, halbtransparent
        fieldBg.setStroke(Color.rgb(180, 120, 40, 0.9));
        fieldBg.setStrokeWidth(3);
        fieldBg.setArcWidth(8);
        fieldBg.setArcHeight(8);

        FXGL.entityBuilder()
                .at(worldX, worldY)
                .view(fieldBg)
                .zIndex(5)          // ← über Map-Layern (0/1/2)
                .buildAndAttach();

        // 2) Einzelne Zellen-Linien
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                double px = worldX + col * CELL_W;
                double py = worldY + row * CELL_H;

                // Zell-Raster (nur Linie, kein Fill → Hintergrund bleibt sichtbar)
                Rectangle cell = new Rectangle(CELL_W, CELL_H);
                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.rgb(220, 180, 80, 0.5));
                cell.setStrokeWidth(1);

                FXGL.entityBuilder()
                        .at(px, py)
                        .view(cell)
                        .zIndex(6)
                        .buildAndAttach();
            }
        }

        // 3) Schild "Acker" oben links


      

        // 4) Konsolenausgabe zur Fehlersuche
        System.out.printf("[FarmField] Feld erstellt bei Welt (%.0f, %.0f) "
                + "bis (%.0f, %.0f)  [%dx%d Zellen à %dpx]%n",
                worldX, worldY,
                worldX + COLS * CELL_W, worldY + ROWS * CELL_H,
                COLS, ROWS, CELL_W);
    }

    // ── Pflanze setzen ────────────────────────────────────────────────────────

    public boolean plant(PlantType type, double playerX, double playerY) {
        int[] cell = worldToCell(playerX, playerY);
        if (cell == null) return false;

        String key = key(cell[0], cell[1]);
        if (plants.containsKey(key)) return false;

        double px = worldX + cell[0] * CELL_W;
        double py = worldY + cell[1] * CELL_H;

        Entity plantEntity = FXGL.entityBuilder()
                .at(px, py)
                .with(new PlantComponent(type))
                .zIndex(7)          // ← über Feld-Hintergrund
                .buildAndAttach();

        plants.put(key, plantEntity);
        return true;
    }

    // ── Bewässern ─────────────────────────────────────────────────────────────

    public boolean waterPlants(double playerX, double playerY) {
        int[] cell = worldToCell(playerX, playerY);
        if (cell == null) return false;

        Entity e = plants.get(key(cell[0], cell[1]));
        if (e == null) return false;

        PlantComponent comp = e.getComponent(PlantComponent.class);
        if (comp.needsWater()) {
            comp.water();
            return true;
        }
        return false;
    }

    // ── Ernten ────────────────────────────────────────────────────────────────

    public PlantType harvest(double playerX, double playerY) {
        int[] cell = worldToCell(playerX, playerY);
        if (cell == null) return null;

        String key = key(cell[0], cell[1]);
        Entity e = plants.get(key);
        if (e == null) return null;

        PlantComponent comp = e.getComponent(PlantComponent.class);
        if (!comp.isReady()) return null;

        PlantType type = comp.getPlantType();
        e.removeFromWorld();
        plants.remove(key);
        return type;
    }

    // ── Hilfsmethoden ────────────────────────────────────────────────────────

    public int[] worldToCell(double wx, double wy) {
        int col = (int) ((wx - worldX) / CELL_W);
        int row = (int) ((wy - worldY) / CELL_H);
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return null;
        return new int[]{col, row};
    }

    public boolean contains(double wx, double wy) {
        return worldToCell(wx, wy) != null;
    }

    public Point2D cellCenter(int col, int row) {
        return new Point2D(worldX + col * CELL_W + CELL_W / 2.0,
                           worldY + row * CELL_H + CELL_H / 2.0);
    }

    public PlantComponent getPlantAt(double wx, double wy) {
        int[] cell = worldToCell(wx, wy);
        if (cell == null) return null;
        Entity e = plants.get(key(cell[0], cell[1]));
        return e == null ? null : e.getComponent(PlantComponent.class);
    }

    public double getWorldX()    { return worldX; }
    public double getWorldY()    { return worldY; }
    public int    getTotalWidth()  { return COLS * CELL_W; }
    public int    getTotalHeight() { return ROWS * CELL_H; }

    private String key(int col, int row) { return col + "," + row; }
}