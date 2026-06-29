package bta.ahaus.lamaDrama.model.entity;

import bta.ahaus.lamaDrama.view.ui.FarmMenu;
import bta.ahaus.lamaDrama.controller.component.PlantComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

/**
 * Das Ackerstück: ein COLS×ROWS-Gitter mit Mausklick-Unterstützung.
 *
 * Klickt der Spieler auf eine Zelle, wird farmMenu.onCellClicked()
 * mit der Weltmitte der Zelle aufgerufen.
 *
 * zIndex-Struktur:
 *   Map-Layer 0/1/2  → Karte
 *   FarmField Boden  → 5
 *   FarmField Gitter → 6
 *   Pflanzen         → 7
 *   Gebäude          → 50
 *   Spieler          → 100
 */
public class FarmField {

    public static final int CELL_W = 64;
    public static final int CELL_H = 64;
    public static final int COLS   = 5;
    public static final int ROWS   = 4;

    private final double worldX;
    private final double worldY;

    private final Map<String, Entity> plants = new HashMap<>();

    // Wird nach dem Konstruktor gesetzt (zirkuläre Abhängigkeit vermeiden)
    private FarmMenu farmMenu;

    // ─────────────────────────────────────────────────────────────────────────

    public FarmField(double worldX, double worldY) {
        this.worldX = worldX;
        this.worldY = worldY;
        buildGrid();
    }

    /** FarmMenu muss nach der Erstellung beider Objekte gesetzt werden. */
    public void setFarmMenu(FarmMenu menu) {
        this.farmMenu = menu;
    }

    // ── Gitter aufbauen ───────────────────────────────────────────────────────

    private void buildGrid() {

        // 1) Erd-Hintergrund für das gesamte Feld
        Rectangle fieldBg = new Rectangle(COLS * CELL_W, ROWS * CELL_H);
        fieldBg.setFill(Color.rgb(101, 67, 33, 0.75));
        fieldBg.setStroke(Color.rgb(180, 120, 40, 0.9));
        fieldBg.setStrokeWidth(3);
        fieldBg.setArcWidth(8);
        fieldBg.setArcHeight(8);

        FXGL.entityBuilder()
                .at(worldX, worldY)
                .view(fieldBg)
                .zIndex(5)
                .buildAndAttach();

        // 2) Einzelne Zellen – klickbar
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                double px = worldX + col * CELL_W;
                double py = worldY + row * CELL_H;

                // Zell-Rechteck (transparenter Fill → Hover-Effekt möglich)
                Rectangle cell = new Rectangle(CELL_W, CELL_H);
                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.rgb(220, 180, 80, 0.5));
                cell.setStrokeWidth(1);

                // Hover-Highlight
                cell.setOnMouseEntered(e -> {
                    if (farmMenu != null && farmMenu.isVisible())
                        cell.setFill(Color.rgb(255, 230, 100, 0.18));
                });
                cell.setOnMouseExited(e -> cell.setFill(Color.TRANSPARENT));

                // Klick → Aktion über FarmMenu
                final double cellCenterX = px + CELL_W / 2.0;
                final double cellCenterY = py + CELL_H / 2.0;
                cell.setOnMouseClicked((MouseEvent e) -> {
                    if (farmMenu != null && farmMenu.isVisible()) {
                        farmMenu.onCellClicked(cellCenterX, cellCenterY);
                        e.consume();
                    }
                });

                FXGL.entityBuilder()
                        .at(px, py)
                        .view(cell)
                        .zIndex(6)
                        .buildAndAttach();
            }
        }

        System.out.printf("[FarmField] Feld erstellt bei (%.0f, %.0f) bis (%.0f, %.0f)  [%dx%d]%n",
                worldX, worldY,
                worldX + COLS * CELL_W, worldY + ROWS * CELL_H,
                COLS, ROWS);
    }

    // ── Pflanze setzen ────────────────────────────────────────────────────────

    public boolean plant(PlantType type, double wx, double wy) {
        int[] cell = worldToCell(wx, wy);
        if (cell == null) return false;

        String key = key(cell[0], cell[1]);
        if (plants.containsKey(key)) return false;

        double px = worldX + cell[0] * CELL_W;
        double py = worldY + cell[1] * CELL_H;

        Entity plantEntity = FXGL.entityBuilder()
                .at(px, py)
                .with(new PlantComponent(type))
                .zIndex(7)
                .buildAndAttach();

        plants.put(key, plantEntity);
        return true;
    }

    // ── Bewässern ─────────────────────────────────────────────────────────────

    public boolean waterPlants(double wx, double wy) {
        int[] cell = worldToCell(wx, wy);
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

    public PlantType harvest(double wx, double wy) {
        int[] cell = worldToCell(wx, wy);
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

    /**
     * Rechnet Weltkoordinaten in Zellen-Index um.
     * Akzeptiert sowohl Mitte als auch beliebige Punkte innerhalb der Zelle.
     */
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

    public double getWorldX()      { return worldX; }
    public double getWorldY()      { return worldY; }
    public int    getTotalWidth()  { return COLS * CELL_W; }
    public int    getTotalHeight() { return ROWS * CELL_H; }

    private String key(int col, int row) { return col + "," + row; }
}