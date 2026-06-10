package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Kontextmenü (Leertaste) auf dem Feld.
 * Zeigt Saatgut-Auswahl, Bewässern- und Ernten-Buttons.
 */
public class FarmMenu {

    private final VBox       root;
    private final Inventory  inventory;
    private final FarmField  field;

    /** Callback: Spieler-Fußposition (Mitte unten) für Zell-Berechnung */
    private java.util.function.Supplier<double[]> playerPosSupplier;

    private boolean visible = false;

    public FarmMenu(Inventory inventory, FarmField field,
                    java.util.function.Supplier<double[]> playerPosSupplier) {
        this.inventory          = inventory;
        this.field              = field;
        this.playerPosSupplier  = playerPosSupplier;

        root = buildUI();
        root.setVisible(false);

        // Zum UI-Layer hinzufügen (fixiert auf dem Bildschirm)
        FXGL.getGameScene().addUINode(root);

        // Position: mittig unten im Bildschirm
        root.setTranslateX(FXGL.getAppWidth() / 2.0 - 220);
        root.setTranslateY(FXGL.getAppHeight() - 320);
    }

    // ── UI aufbauen ───────────────────────────────────────────────────────────

    private VBox buildUI() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(14));
        panel.setPrefWidth(440);
        panel.setBackground(new Background(new BackgroundFill(
                Color.rgb(30, 18, 8, 0.92),
                new CornerRadii(12), Insets.EMPTY)));
        panel.setBorder(new Border(new BorderStroke(
                Color.GOLDENROD, BorderStrokeStyle.SOLID,
                new CornerRadii(12), new BorderWidths(2))));

        Label title = new Label("🌱 Ackerbau");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.LIGHTGOLDENRODYELLOW);

        Label coinLabel = new Label();
        coinLabel.setTextFill(Color.GOLD);
        coinLabel.setFont(Font.font(14));

        // Saatgut-Zeile (alle PlantTypes)
        HBox seedRow = new HBox(8);
        seedRow.setAlignment(Pos.CENTER_LEFT);
        for (PlantType type : PlantType.values()) {
            Button btn = makeSeedButton(type, coinLabel);
            seedRow.getChildren().add(btn);
        }

        // Aktion-Buttons
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER);

        Button waterBtn = makeActionButton("💧 Bewässern", Color.DEEPSKYBLUE);
        waterBtn.setOnAction(e -> {
            double[] pos = playerPosSupplier.get();
            boolean watered = field.waterPlants(pos[0], pos[1]);
            showFeedback(watered ? "Bewässert! 💧" : "Keine Pflanze braucht Wasser hier.", watered);
        });

        Button harvestBtn = makeActionButton("🌾 Ernten", Color.GOLDENROD);
        harvestBtn.setOnAction(e -> {
            double[] pos = playerPosSupplier.get();
            PlantType harvested = field.harvest(pos[0], pos[1]);
            if (harvested != null) {
                inventory.addItem(harvested, harvested.harvestAmount);
                showFeedback("+" + harvested.harvestAmount + " " + harvested.displayName + " geerntet! " + harvested.emoji, true);
            } else {
                PlantComponent comp = field.getPlantAt(pos[0], pos[1]);
                if (comp != null && comp.needsWater())
                    showFeedback("Pflanze braucht Wasser! 💧", false);
                else
                    showFeedback("Hier ist noch nichts reif.", false);
            }
            refreshCoinLabel(coinLabel);
        });

        Button closeBtn = makeActionButton("✖ Schließen", Color.LIGHTCORAL);
        closeBtn.setOnAction(e -> hide());

        actionRow.getChildren().addAll(waterBtn, harvestBtn, closeBtn);

        // Feedback-Label
        Label feedback = new Label("");
        feedback.setTextFill(Color.LIGHTGREEN);
        feedback.setFont(Font.font(13));
        feedback.setWrapText(true);
        feedbackLabel = feedback;

        panel.getChildren().addAll(title, coinLabel, new Label("── Saatgut ──") {{
            setTextFill(Color.BURLYWOOD); setFont(Font.font(12));
        }}, seedRow, new Label("── Aktionen ──") {{
            setTextFill(Color.BURLYWOOD); setFont(Font.font(12));
        }}, actionRow, feedback);

        // Beim Anzeigen Münzen aktualisieren
        panel.visibleProperty().addListener((obs, o, n) -> {
            if (n) refreshCoinLabel(coinLabel);
        });

        return panel;
    }

    // kleines Hilfsfeld, damit onAdded darauf zugreifen kann
    private Label feedbackLabel;

    private Button makeSeedButton(PlantType type, Label coinLabel) {
        String cost = type.seedCost == 0 ? "kostenlos" : type.seedCost + "€";
        Button btn = new Button(type.emoji + " " + type.displayName + "\n" + cost);
        btn.setFont(Font.font(12));
        btn.setWrapText(true);
        btn.setPrefWidth(68);
        btn.setStyle("""
                -fx-background-color: #3a2010;
                -fx-text-fill: #f5deb3;
                -fx-border-color: #8b6914;
                -fx-border-width: 1;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """);
        btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: #5a3520;
                -fx-text-fill: #ffe4b5;
                -fx-border-color: goldenrod;
                -fx-border-width: 1;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """));
        btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: #3a2010;
                -fx-text-fill: #f5deb3;
                -fx-border-color: #8b6914;
                -fx-border-width: 1;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """));
        btn.setOnAction(e -> {
            if (!inventory.spend(type.seedCost)) {
                showFeedback("Nicht genug Münzen! ", false);
                return;
            }
            double[] pos = playerPosSupplier.get();
            boolean planted = field.plant(type, pos[0], pos[1]);
            if (!planted) {
                inventory.addCoins(type.seedCost); // Rückerstattung
                showFeedback("Diese Zelle ist bereits belegt oder außerhalb!", false);
            } else {
                showFeedback(type.displayName + " gepflanzt! " + type.emoji + "  (wächst " + type.growSeconds + "s)", true);
            }
            refreshCoinLabel(coinLabel);
        });
        return btn;
    }

    private Button makeActionButton(String text, Color color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        btn.setTextFill(color);
        btn.setStyle("""
                -fx-background-color: #2a1808;
                -fx-border-color: #8b6914;
                -fx-border-width: 1;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 6 14;
                """);
        btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: #4a2818;
                -fx-border-color: goldenrod;
                -fx-border-width: 1;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 6 14;
                """));
        btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: #2a1808;
                -fx-border-color: #8b6914;
                -fx-border-width: 1;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 6 14;
                """));
        return btn;
    }

    private void refreshCoinLabel(Label coinLabel) {
        coinLabel.setText("💰 Münzen: " + inventory.getCoins());
    }

    private void showFeedback(String msg, boolean success) {
        feedbackLabel.setText(msg);
        feedbackLabel.setTextFill(success ? Color.LIGHTGREEN : Color.TOMATO);
    }

    // ── Sichtbarkeit ──────────────────────────────────────────────────────────

    public void toggle() {
        if (visible) hide(); else show();
    }

    public void show() {
        visible = true;
        root.setVisible(true);
    }

    public void hide() {
        visible = false;
        root.setVisible(false);
    }

    public boolean isVisible() { return visible; }
}