package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HudDisplay {
    private final PlayerStats stats;
    private final LamaDreck   lamaDreck;
    private final VBox        root;

    private final Label     xpLabel    = new Label();
    private final Label     dreckLabel = new Label();
    private final Rectangle dreckBalken = new Rectangle(0, 12);  // Breite wird in refresh() gesetzt

    public HudDisplay(PlayerStats stats, LamaDreck lamaDreck) {
        this.stats     = stats;
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
        panel.setBackground(new Background(new BackgroundFill(
                Color.rgb(20, 12, 4, 0.80),
                new CornerRadii(10), Insets.EMPTY)));
        panel.setBorder(new Border(new BorderStroke(
                Color.GOLDENROD, BorderStrokeStyle.SOLID,
                new CornerRadii(10), new BorderWidths(1.5))));

        Label title = new Label("⭐ Stats");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        title.setTextFill(Color.LIGHTGOLDENRODYELLOW);

        xpLabel.setFont(Font.font(13));
        xpLabel.setTextFill(Color.LIGHTGREEN);

        dreckLabel.setFont(Font.font(13));
        dreckLabel.setTextFill(Color.WHEAT);

        // Hintergrund des Balkens (grau)
        Rectangle balkenHintergrund = new Rectangle(145, 12);
        balkenHintergrund.setFill(Color.rgb(60, 60, 60));
        balkenHintergrund.setArcWidth(6);
        balkenHintergrund.setArcHeight(6);

        // Dreck-Balken (grün → gelb → rot)
        dreckBalken.setArcWidth(6);
        dreckBalken.setArcHeight(6);

        // Balken übereinander stapeln
        StackPane balkenPane = new StackPane(balkenHintergrund, dreckBalken);
        balkenPane.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        panel.getChildren().addAll(title, xpLabel, dreckLabel, balkenPane);
        return panel;
    }

    public void refresh() {
        xpLabel.setText("✨ XP: "      + stats.getXP());
        
        int prozent = lamaDreck.getDreckProzent();
        dreckLabel.setText("🐑 Dreck: " + prozent + "%");

        // Balken-Breite anpassen (max 145px)
        double breite = (prozent / 100.0) * 145;
        dreckBalken.setWidth(breite);

        // Farbe: grün → gelb → rot
        if (prozent < 50) {
            dreckBalken.setFill(Color.rgb(80, 180, 80));   // grün
        } else if (prozent < 80) {
            dreckBalken.setFill(Color.rgb(220, 180, 0));   // gelb
        } else {
            dreckBalken.setFill(Color.rgb(200, 60, 60));   // rot
        }
    }
}