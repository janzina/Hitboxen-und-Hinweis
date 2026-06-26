package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.EnumMap;
import java.util.Map;

/**
 * HUD-Inventar in der oberen rechten Ecke des Bildschirms.
 * Wird jedes Update-Frame aktualisiert.
 */
public class InventoryUI {

    private final Inventory inventory;
    private final VBox      root;

    private final Label coinLabel = new Label();
    private final Map<PlantType, Label> itemLabels = new EnumMap<>(PlantType.class);

    public InventoryUI(Inventory inventory) {
        this.inventory = inventory;
        root = buildUI();
        FXGL.getGameScene().addUINode(root);

        // Rechts oben positionieren
        root.setTranslateX(FXGL.getAppWidth() - 180);
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

        Label title = new Label("🎒 Inventar");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        title.setTextFill(Color.LIGHTGOLDENRODYELLOW);

        coinLabel.setFont(Font.font(13));
        coinLabel.setTextFill(Color.GOLD);

        panel.getChildren().addAll(title, coinLabel);

        for (PlantType type : PlantType.values()) {
            Label lbl = new Label(type.emoji + " " + type.displayName + ": 0");
            lbl.setFont(Font.font(12));
            lbl.setTextFill(Color.WHEAT);
            itemLabels.put(type, lbl);
            panel.getChildren().add(lbl);
        }

        // Hinweis am unteren Rand
        Label hint = new Label("LEERTASTE = Menü\n(auf dem Feld)");
        hint.setFont(Font.font(10));
        hint.setTextFill(Color.GRAY);
        hint.setAlignment(Pos.CENTER);
        panel.getChildren().add(hint);

        return panel;
    }
    
    /** Muss regelmäßig (onUpdate) aufgerufen werden. */
    public void refresh() {
        coinLabel.setText("💰 " + inventory.getCoins());

        for (PlantType type : PlantType.values()) {
            int seeds = inventory.getSeedCount(type);
            int crops = inventory.getHarvestCount(type);

            Label lbl = itemLabels.get(type);

          if (type.isSpezialitaet()) {

                lbl.setText(
                        type.emoji + " "
                        + type.displayName
                        + ": "
                        + seeds
                );

            } else {

                lbl.setText(
                        type.displayName
                        + "\n📦 Saatgut: "
                        + seeds
                        + "\n"
                        + type.emoji
                        + " Ernte: "
                        + crops
                );
            }
        }
    }
}