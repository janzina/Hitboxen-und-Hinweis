package bta.ahaus.lamaDrama.view.ui;

import bta.ahaus.lamaDrama.controller.component.PlantComponent;
import bta.ahaus.lamaDrama.controller.component.WateringAnimation;
import bta.ahaus.lamaDrama.model.data.Inventory;
import bta.ahaus.lamaDrama.model.entity.PlantType;
import bta.ahaus.lamaDrama.model.entity.FarmField;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.function.Supplier;

public class FarmMenu {

    // ── Werkzeugmodus ─────────────────────────────────────────────────────────
    public enum Tool { NONE, SEED, WATER, HARVEST }

    private Tool      activeTool   = Tool.NONE;
    private PlantType selectedSeed = null;

    // ── Refs ──────────────────────────────────────────────────────────────────
    private final VBox               root;
    private final Inventory          inventory;
    private final FarmField          field;
    private final Supplier<double[]> playerPosSupplier;

    private WateringAnimation wateringAnimation;

    // UI-Refs für Updates
    private Label     feedbackLabel;
    private Label     coinLabel;
    private HBox      seedRow;
    private StackPane waterToolBtn;
    private StackPane harvestToolBtn;

    private boolean visible = false;

    // ── Farben / Stil ─────────────────────────────────────────────────────────
    private static final String BG_DARK   = "-fx-background-color: #2b1a0e;";
    private static final String BG_MID    = "-fx-background-color: #3d2410;";
    private static final String BG_HOVER  = "-fx-background-color: #5a3520;";
    private static final String BG_ACTIVE = "-fx-background-color: #7a4a10;";
    private static final Color  GOLD      = Color.web("#d4a017");
    private static final Color  LIGHT_TAN = Color.web("#f5deb3");
    private static final Color  DIM_TAN   = Color.web("#a08060");

    // ─────────────────────────────────────────────────────────────────────────

    public FarmMenu(Inventory inventory, FarmField field,
                    Supplier<double[]> playerPosSupplier) {
        this.inventory         = inventory;
        this.field             = field;
        this.playerPosSupplier = playerPosSupplier;

        wateringAnimation = new WateringAnimation();

        root = buildUI();
        root.setVisible(false);

        FXGL.getGameScene().addUINode(root);
        root.setTranslateX(FXGL.getAppWidth() / 2.0 - 270);
        root.setTranslateY(10);
    }

    // ── UI aufbauen ───────────────────────────────────────────────────────────

    private VBox buildUI() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14, 18, 14, 18));
        panel.setPrefWidth(540);

        panel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0.0, Color.web("#3d2410")),
                        new Stop(1.0, Color.web("#1e0f06"))),
                new CornerRadii(14), Insets.EMPTY)));

        panel.setBorder(new Border(new BorderStroke(
                GOLD, BorderStrokeStyle.SOLID,
                new CornerRadii(14), new BorderWidths(2))));

        DropShadow shadow = new DropShadow(18, Color.BLACK);
        shadow.setSpread(0.2);
        panel.setEffect(shadow);

        // ── Titelzeile ───────────────────────────────────────────────────────
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🌾  Ackerbau");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        title.setTextFill(GOLD);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        coinLabel = new Label("💰 " + inventory.getCoins());
        coinLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        coinLabel.setTextFill(GOLD);

        Label closeBtn = new Label("✕");
        closeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        closeBtn.setTextFill(DIM_TAN);
        closeBtn.setStyle("-fx-cursor: hand;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setTextFill(Color.TOMATO));
        closeBtn.setOnMouseExited(e  -> closeBtn.setTextFill(DIM_TAN));
        closeBtn.setOnMouseClicked(e -> hide());

        titleRow.getChildren().addAll(title, spacer, coinLabel, closeBtn);

        // ── Saatgut-Zeile ────────────────────────────────────────────────────
        Label seedLabel = sectionLabel("Saatgut");
        seedRow = new HBox(8);
        seedRow.setAlignment(Pos.CENTER_LEFT);
        for (PlantType type : PlantType.values()) {
            if (!type.isPflanze()) continue;
            seedRow.getChildren().add(makeSeedButton(type));
        }

        // ── Werkzeuge ────────────────────────────────────────────────────────
        Label toolLabel = sectionLabel("Werkzeuge");
        HBox toolRow = new HBox(12);
        toolRow.setAlignment(Pos.CENTER_LEFT);

        waterToolBtn   = makeToolButton(Tool.WATER);
        harvestToolBtn = makeToolButton(Tool.HARVEST);
        toolRow.getChildren().addAll(waterToolBtn, harvestToolBtn);

        // ── Feedback-Zeile ───────────────────────────────────────────────────
        feedbackLabel = new Label("Wähle ein Werkzeug oder Saatgut, dann klicke auf eine Zelle.");
        feedbackLabel.setFont(Font.font("Arial", 13));
        feedbackLabel.setTextFill(DIM_TAN);
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMaxWidth(500);

        panel.getChildren().addAll(
                titleRow, makeDivider(),
                seedLabel, seedRow,
                makeDivider(),
                toolLabel, toolRow,
                makeDivider(),
                feedbackLabel);

        panel.visibleProperty().addListener((obs, o, n) -> {
            if (n) refreshCoinLabel();
        });

        return panel;
    }

    // ── Saatgut-Button ────────────────────────────────────────────────────────

    private StackPane makeSeedButton(PlantType type) {
        StackPane btn = new StackPane();
        btn.setPrefSize(72, 90);
        btn.setStyle(BG_MID + roundBorder("#6b4a20", 10));

        Group icon = SeedIcon.create(type, 44);
        icon.setTranslateY(-6);

        Label nameLabel = new Label(type.displayName);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        nameLabel.setTextFill(LIGHT_TAN);
        nameLabel.setTranslateY(28);

        btn.getChildren().addAll(icon, nameLabel);

        Tooltip tip = new Tooltip(type.displayName + "  " + type.emoji
                + "\nWächst in " + type.growSeconds + "s");
        tip.setStyle("-fx-background-color: #2b1a0e; -fx-text-fill: #f5deb3; "
                   + "-fx-font-size: 12; -fx-border-color: #d4a017; -fx-border-width: 1;");
        Tooltip.install(btn, tip);

        btn.setOnMouseEntered(e -> btn.setStyle(BG_HOVER + roundBorder("#d4a017", 10)));
        btn.setOnMouseExited(e  -> {
            if (activeTool == Tool.SEED && selectedSeed == type)
                btn.setStyle(BG_ACTIVE + roundBorder("#d4a017", 10));
            else
                btn.setStyle(BG_MID + roundBorder("#6b4a20", 10));
        });

        btn.setOnMouseClicked(e -> {
            deselectAllSeeds();
            deselectToolButtons();
            selectedSeed = type;
            activeTool   = Tool.SEED;
            btn.setStyle(BG_ACTIVE + roundBorder("#d4a017", 10));
            showFeedback("Bereit zum Pflanzen: " + type.displayName + "  " + type.emoji
                       + " – Zelle anklicken", true);
        });

        btn.setUserData(type);
        return btn;
    }

    private void deselectAllSeeds() {
        for (var node : seedRow.getChildren()) {
            if (node instanceof StackPane sp)
                sp.setStyle(BG_MID + roundBorder("#6b4a20", 10));
        }
    }

    // ── Werkzeug-Button ───────────────────────────────────────────────────────

    private StackPane makeToolButton(Tool tool) {
        StackPane btn = new StackPane();
        btn.setPrefSize(72, 90);
        btn.setStyle(BG_MID + roundBorder("#6b4a20", 10));

        Group icon     = tool == Tool.WATER ? drawWateringCan() : drawSickle();
        String tipText = tool == Tool.WATER
                ? "Gießkanne – Zelle klicken zum Bewässern"
                : "Sichel – Zelle klicken zum Ernten";
        String name    = tool == Tool.WATER ? "Gießkanne" : "Sichel";

        icon.setTranslateY(-6);
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        nameLabel.setTextFill(LIGHT_TAN);
        nameLabel.setTranslateY(28);
        btn.getChildren().addAll(icon, nameLabel);

        Tooltip tip = new Tooltip(tipText);
        tip.setStyle("-fx-background-color: #2b1a0e; -fx-text-fill: #f5deb3; "
                   + "-fx-font-size: 12; -fx-border-color: #d4a017; -fx-border-width: 1;");
        Tooltip.install(btn, tip);

        btn.setOnMouseEntered(e -> {
            if (activeTool != tool)
                btn.setStyle(BG_HOVER + roundBorder("#d4a017", 10));
        });
        btn.setOnMouseExited(e -> {
            if (activeTool != tool)
                btn.setStyle(BG_MID + roundBorder("#6b4a20", 10));
        });
        btn.setOnMouseClicked(e -> selectTool(btn, tool));

        return btn;
    }

    // ── Gießkannen-Vektor ─────────────────────────────────────────────────────

    private static Group drawWateringCan() {
        Group g = new Group();
        Color dark  = Color.web("#2a6090");
        Color light = Color.web("#7ab8e8");

        Polygon can = new Polygon(4.0, 18.0, 38.0, 18.0, 34.0, 38.0, 8.0, 38.0);
        can.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, light), new Stop(1, dark)));
        can.setStroke(dark); can.setStrokeWidth(1.2);
        can.setStrokeLineJoin(StrokeLineJoin.ROUND);
        g.getChildren().add(can);

        Rectangle lid = new Rectangle(4, 14, 34, 6);
        lid.setArcWidth(4); lid.setArcHeight(4);
        lid.setFill(dark); lid.setStroke(dark.darker()); lid.setStrokeWidth(1);
        g.getChildren().add(lid);

        Line spout = new Line(4, 22, -12, 10);
        spout.setStroke(dark); spout.setStrokeWidth(5);
        spout.setStrokeLineCap(StrokeLineCap.ROUND);
        g.getChildren().add(spout);

        Rectangle rose = new Rectangle(-18, 6, 8, 6);
        rose.setArcWidth(2); rose.setArcHeight(2);
        rose.setFill(dark); rose.setStroke(dark.darker()); rose.setStrokeWidth(0.8);
        g.getChildren().add(rose);

        for (int i = 0; i < 4; i++) {
            Circle drop = new Circle(-16 + i * 2.5, 14 + i * 3, 1.2, Color.web("#a0d8f8"));
            drop.setOpacity(0.8);
            g.getChildren().add(drop);
        }

        Arc handle = new Arc(21, 8, 14, 10, 20, 140);
        handle.setType(ArcType.OPEN);
        handle.setFill(Color.TRANSPARENT);
        handle.setStroke(dark); handle.setStrokeWidth(3.5);
        handle.setStrokeLineCap(StrokeLineCap.ROUND);
        g.getChildren().add(handle);

        g.setTranslateX(10); g.setTranslateY(2);
        return g;
    }

    // ── Sichel-Vektor ─────────────────────────────────────────────────────────

    private static Group drawSickle() {
        Group g = new Group();
        Color blade = Color.web("#c8c0a0");
        Color dark  = Color.web("#808060");
        Color woodD = Color.web("#5a3a1a");

        Arc arc = new Arc(22, 28, 20, 18, 30, 200);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(blade); arc.setStrokeWidth(5);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        g.getChildren().add(arc);

        Arc arcShine = new Arc(22, 28, 17, 15, 40, 160);
        arcShine.setType(ArcType.OPEN);
        arcShine.setFill(Color.TRANSPARENT);
        arcShine.setStroke(Color.web("#e8e0c8"));
        arcShine.setStrokeWidth(1.5); arcShine.setOpacity(0.6);
        g.getChildren().add(arcShine);

        Rectangle handle = new Rectangle(18, 30, 7, 16);
        handle.setArcWidth(4); handle.setArcHeight(4);
        handle.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#b07848")), new Stop(1, woodD)));
        handle.setStroke(woodD); handle.setStrokeWidth(1);
        g.getChildren().add(handle);

        Rectangle ring = new Rectangle(17, 33, 9, 3);
        ring.setFill(dark); ring.setArcWidth(2); ring.setArcHeight(2);
        g.getChildren().add(ring);

        g.setTranslateX(2); g.setTranslateY(0);
        return g;
    }

    // ── Tool-Auswahl ──────────────────────────────────────────────────────────

    private void selectTool(StackPane btn, Tool tool) {
        deselectAllSeeds();
        deselectToolButtons();
        selectedSeed = null;
        activeTool   = tool;
        btn.setStyle(BG_ACTIVE + roundBorder("#d4a017", 10));
        showFeedback(tool == Tool.WATER
                ? "Gießkanne aktiv – Zelle anklicken 💧"
                : "Sichel aktiv – Zelle anklicken 🌾", true);
    }

    private void deselectToolButtons() {
        if (waterToolBtn   != null) waterToolBtn.setStyle(BG_MID  + roundBorder("#6b4a20", 10));
        if (harvestToolBtn != null) harvestToolBtn.setStyle(BG_MID + roundBorder("#6b4a20", 10));
    }

    // ── Aktion auf Zelle ausführen ────────────────────────────────────────────

    public void onCellClicked(double worldX, double worldY) {
        switch (activeTool) {
            case SEED -> {
                if (selectedSeed == null) {
                    showFeedback("Wähle zuerst ein Saatgut!", false);
                    return;
                }
                if (inventory.getSeedCount(selectedSeed) <= 0) {
                    showFeedback("❌ Kein Saatgut vorhanden!", false);
                    return;
                }
                boolean planted = field.plant(selectedSeed, worldX, worldY);
                if (planted) {
                    inventory.removeSeed(selectedSeed, 1);
                    showFeedback(selectedSeed.displayName + " gepflanzt! "
                               + selectedSeed.emoji
                               + "  (wächst " + selectedSeed.growSeconds + "s)", true);
                } else {
                    showFeedback("Diese Zelle ist bereits belegt!", false);
                }
            }
            case WATER -> {
                boolean watered = field.waterPlants(worldX, worldY);
                if (watered) {
                    wateringAnimation.play(worldX, worldY);
                    showFeedback("Bewässert! 💧", true);
                } else {
                    PlantComponent comp = field.getPlantAt(worldX, worldY);
                    if (comp == null)
                        showFeedback("Hier wächst noch nichts.", false);
                    else
                        showFeedback("Braucht noch kein Wasser.", false);
                }
            }
            case HARVEST -> {
                PlantType harvested = field.harvest(worldX, worldY);
                if (harvested != null) {
                    inventory.addItem(harvested, harvested.harvestAmount);
                    showFeedback("+" + harvested.harvestAmount + " "
                               + harvested.displayName + " geerntet! "
                               + harvested.emoji, true);
                    refreshCoinLabel();
                } else {
                    PlantComponent comp = field.getPlantAt(worldX, worldY);
                    if (comp != null && comp.needsWater())
                        showFeedback("Pflanze braucht noch Wasser! 💧", false);
                    else if (comp != null)
                        showFeedback("Noch nicht reif.", false);
                    else
                        showFeedback("Hier ist nichts zum Ernten.", false);
                }
            }
            default -> showFeedback("Wähle zuerst ein Werkzeug oder Saatgut.", false);
        }
    }

    // ── Hilfsmethoden ─────────────────────────────────────────────────────────

    private Label sectionLabel(String text) {
        Label l = new Label(text.toUpperCase());
        l.setFont(Font.font("Georgia", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#9b7b40"));
        l.setPadding(new Insets(4, 0, 0, 0));
        return l;
    }

    private Rectangle makeDivider() {
        Rectangle r = new Rectangle(504, 1);
        r.setFill(Color.web("#6b4a20", 0.6));
        return r;
    }

    private void refreshCoinLabel() {
        if (coinLabel != null)
            coinLabel.setText("💰 " + inventory.getCoins());
    }

    private void showFeedback(String msg, boolean success) {
        if (feedbackLabel == null) return;
        feedbackLabel.setText(msg);
        feedbackLabel.setTextFill(success ? Color.web("#7ec850") : Color.web("#e05050"));
    }

    private static String roundBorder(String color, int radius) {
        return " -fx-border-color: " + color + "; -fx-border-width: 1.5;"
             + " -fx-border-radius: " + radius + "; -fx-background-radius: " + radius + ";";
    }

    // ── Sichtbarkeit ──────────────────────────────────────────────────────────

    public void toggle()               { if (visible) hide(); else show(); }
    public void show()                 { visible = true;  root.setVisible(true);  }
    public void hide()                 { visible = false; root.setVisible(false); }
    public boolean isVisible()         { return visible; }
    public Tool    getActiveTool()     { return activeTool; }
    public javafx.scene.Node getRootNode() { return root; }
}