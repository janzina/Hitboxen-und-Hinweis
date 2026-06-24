package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HudDisplay {
    private final LamaDreck lamaDreck;
    private final VBox      root;

    private final Label     xpLabel      = new Label();
    private final Label     coinsLabel   = new Label();
    private final Label     dreckLabel   = new Label();
    private final Label     hungerLabel  = new Label();

    private final Rectangle dreckBalken  = new Rectangle(0, 12);
    private final Rectangle hungerBalken = new Rectangle(0, 12);

    public HudDisplay(LamaDreck lamaDreck) {
        this.lamaDreck = lamaDreck;
        root = buildUI();
        FXGL.getGameScene().addUINode(root);
        root.setTranslateX(10);
        root.setTranslateY(10);
    }

    private VBox buildUI() {
        VBox panel = new VBox(4);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(165);
        panel.setBackground(
        new javafx.scene.layout.Background(
            new BackgroundFill(
                Color.rgb(20, 12, 4, 0.80),
                new CornerRadii(10),
                Insets.EMPTY
            )
        )
    );
        panel.setBorder(new Border(new BorderStroke(
                Color.GOLDENROD, BorderStrokeStyle.SOLID,
                new CornerRadii(10), new BorderWidths(1.5))));

        // ── Titel ─────────────────────────────────────────────────────────────
        Label title = new Label("⭐ Stats");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        title.setTextFill(Color.LIGHTGOLDENRODYELLOW);

        // ── XP & Coins ────────────────────────────────────────────────────────
        xpLabel.setFont(Font.font(13));
        xpLabel.setTextFill(Color.LIGHTGREEN);

        coinsLabel.setFont(Font.font(13));
        coinsLabel.setTextFill(Color.GOLD);

        // ── Trennlinie ────────────────────────────────────────────────────────
        Rectangle trennlinie = new Rectangle(145, 1);
        trennlinie.setFill(Color.color(1, 1, 1, 0.15));

        // ── Dreck ─────────────────────────────────────────────────────────────
        dreckLabel.setFont(Font.font(13));
        dreckLabel.setTextFill(Color.WHEAT);
        dreckBalken.setArcWidth(6);
        dreckBalken.setArcHeight(6);

        // ── Hunger ────────────────────────────────────────────────────────────
        hungerLabel.setFont(Font.font(13));
        hungerLabel.setTextFill(Color.WHEAT);
        hungerBalken.setArcWidth(6);
        hungerBalken.setArcHeight(6);

        panel.getChildren().addAll(
                title,
                xpLabel,
                coinsLabel,
                trennlinie,
                dreckLabel,
                buildBalken(dreckBalken),
                hungerLabel,
                buildBalken(hungerBalken)
        );
        return panel;
    }

    private StackPane buildBalken(Rectangle vordergrund) {
        Rectangle hintergrund = new Rectangle(145, 12);
        hintergrund.setFill(Color.rgb(60, 60, 60));
        hintergrund.setArcWidth(6);
        hintergrund.setArcHeight(6);
        StackPane pane = new StackPane(hintergrund, vordergrund);
        pane.setAlignment(Pos.CENTER_LEFT);
        return pane;
    }

    // ── Refresh – wird jeden Frame von MVerwaltung.onUpdate() aufgerufen ──────

    public void refresh() {
        PlayerStats gs = PlayerStats.getInstance();

        // XP & Coins
        xpLabel.setText("✨ XP: "   + gs.getXP());

        // Dreck
        int dreck = lamaDreck.getDreckProzent();
        dreckLabel.setText("🐑 Dreck: " + dreck + "%");
        dreckBalken.setWidth((dreck / 100.0) * 145);
        if (dreck < 50) {
            dreckBalken.setFill(Color.rgb(80, 180, 80));
        } else if (dreck < 80) {
            dreckBalken.setFill(Color.rgb(220, 180, 0));
        } else {
            dreckBalken.setFill(Color.rgb(200, 60, 60));
        }

        // Hunger – liest direkt aus GameStats, FeedingGameWindow schreibt dort rein
        double hunger = gs.getHunger();
        hungerBalken.setWidth((hunger / 100.0) * 145);
        if (hunger >= 70) {
            hungerLabel.setText("🌿 Hunger: " + (int) hunger + "%");
            hungerLabel.setTextFill(Color.LIGHTGREEN);
            hungerBalken.setFill(Color.rgb(80, 180, 80));
        } else if (hunger >= 40) {
            hungerLabel.setText("🌿 Hunger: " + (int) hunger + "% – hungrig");
            hungerLabel.setTextFill(Color.rgb(255, 180, 50));
            hungerBalken.setFill(Color.rgb(220, 140, 0));
        } else {
            hungerLabel.setText("🌿 Hunger: " + (int) hunger + "% – sehr hungrig!");
            hungerLabel.setTextFill(Color.rgb(255, 80, 80));
            hungerBalken.setFill(Color.rgb(200, 60, 60));
        }
    }
}