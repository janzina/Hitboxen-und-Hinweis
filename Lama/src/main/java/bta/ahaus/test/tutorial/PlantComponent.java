package bta.ahaus.lamaDrama.controller.component;

import bta.ahaus.lamaDrama.model.entity.PlantType;
import bta.ahaus.lamaDrama.model.entity.FarmField;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Verwaltet das Wachstum, Bewässerungs-Bedarf und die Ernte einer einzelnen Pflanze.
 *
 * Zustände:
 *   GROWING_DRY  → wächst, braucht noch kein Wasser
 *   NEEDS_WATER  → halbe Zeit erreicht, muss bewässert werden
 *   GROWING_WET  → bewässert, wächst weiter
 *   READY        → kann geerntet werden
 */
public class PlantComponent extends Component {

    public enum GrowState { GROWING_DRY, NEEDS_WATER, GROWING_WET, READY }

    private final PlantType type;
    private double elapsed   = 0;
    private GrowState state  = GrowState.GROWING_DRY;

    // Visuelle Elemente
    private Rectangle background;
    private Rectangle progressBar;
    private Text      label;
    private Circle    waterIndicator;
    private StackPane root;

    static final int CELL_W = FarmField.CELL_W;
    static final int CELL_H = FarmField.CELL_H;

    public PlantComponent(PlantType type) {
        this.type = type;
    }

    @Override
    public void onAdded() {
        // Hintergrund-Erde
        background = new Rectangle(CELL_W - 4, CELL_H - 4, Color.SADDLEBROWN);
        background.setArcWidth(6);
        background.setArcHeight(6);
        background.setStroke(Color.SIENNA);
        background.setStrokeWidth(2);

        // Fortschrittsbalken (grün)
        progressBar = new Rectangle(0, 6, Color.LIMEGREEN);
        progressBar.setTranslateX(-(CELL_W - 4) / 2.0);
        progressBar.setTranslateY((CELL_H - 4) / 2.0 - 8);

        // Emoji-Label der Pflanze
        label = new Text(type.emoji);
        label.setFont(Font.font(22));

        // Blauer Kreis = "braucht Wasser"
        waterIndicator = new Circle(7, Color.DEEPSKYBLUE);
        waterIndicator.setStroke(Color.BLUE);
        waterIndicator.setStrokeWidth(1.5);
        waterIndicator.setTranslateX(CELL_W / 2.0 - 12);
        waterIndicator.setTranslateY(-(CELL_H / 2.0 - 12));
        waterIndicator.setVisible(false);

        root = new StackPane(background, progressBar, label, waterIndicator);
        root.setPrefSize(CELL_W, CELL_H);
        root.setMouseTransparent(true);

        entity.getViewComponent().addChild(root);
    }

    @Override
    public void onUpdate(double tpf) {
        if (state == GrowState.READY) return;
        if (state == GrowState.NEEDS_WATER) return; // pausiert bis Wasser

        elapsed += tpf;
        double total      = type.growSeconds;
        double half       = total / 2.0;

        if (state == GrowState.GROWING_DRY && elapsed >= half) {
            state = GrowState.NEEDS_WATER;
            waterIndicator.setVisible(true);
            elapsed = half; // einfrieren
            return;
        }

        if (state == GrowState.GROWING_WET && elapsed >= total) {
            state = GrowState.READY;
            label.setText("✅");
            progressBar.setWidth(CELL_W - 4);
            progressBar.setFill(Color.GOLD);
            return;
        }

        // Fortschrittsbalken aktualisieren
        double progress = Math.min(elapsed / total, 1.0);
        progressBar.setWidth(progress * (CELL_W - 4));
        progressBar.setTranslateX(-(CELL_W - 4) / 2.0 + (progress * (CELL_W - 4)) / 2.0);

        // Pflanze visuell größer werden lassen
        double scale = 0.4 + 0.6 * progress;
        label.setScaleX(scale);
        label.setScaleY(scale);
    }

    // ── Öffentliche API ───────────────────────────────────────────────────────

    public GrowState getState() { return state; }
    public PlantType getPlantType() { return type; }

    /** Bewässert die Pflanze – wechselt in GROWING_WET. */
    public void water() {
        if (state == GrowState.NEEDS_WATER) {
            state = GrowState.GROWING_WET;
            waterIndicator.setVisible(false);
            background.setFill(Color.PERU); // leicht feuchter
        }
    }

    /** Gibt true zurück, wenn die Pflanze geerntet werden kann. */
    public boolean isReady() { return state == GrowState.READY; }

    /** Gibt true zurück, wenn die Pflanze Wasser braucht. */
    public boolean needsWater() { return state == GrowState.NEEDS_WATER; }
}