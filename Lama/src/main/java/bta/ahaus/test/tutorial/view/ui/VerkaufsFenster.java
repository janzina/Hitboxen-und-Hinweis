package bta.ahaus.lamaDrama.view.ui;

import bta.ahaus.lamaDrama.model.data.Inventory;
import bta.ahaus.lamaDrama.model.entity.PlantType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VerkaufsFenster {

    public static void open(Inventory inventory) {

        Stage stage = new Stage();
        stage.setTitle("Kasse");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label titel = new Label("🛒 Was möchtest du verkaufen?");
        root.getChildren().add(titel);

        boolean etwasVorhanden = false;

        for (PlantType type : PlantType.values()) {

            // Spezialitäten nicht verkaufbar (außer Nugget)
            if (type.isSpezialitaet() && type != PlantType.NUGGET) continue;

            int anzahl = inventory.getItem(type);
            if (anzahl <= 0) continue;

            etwasVorhanden = true;

            // Nugget hat festen sellPrice, Rest berechnet
            int verkaufspreis = type == PlantType.NUGGET
               ? 50
                : (int) Math.ceil(type.seedCost * 1.5);

            Button verkaufen = new Button(
                type.emoji + " " + type.displayName +
                " (" + anzahl + ")  →  " + verkaufspreis + " Münzen"
            );
            verkaufen.setPrefWidth(280);
            verkaufen.setOnAction(e -> {
                if (inventory.removeItem(type, 1)) {
                    inventory.addCoins(verkaufspreis);
                    stage.close();
                    open(inventory);
                }
            });

            root.getChildren().add(verkaufen);
        }

        if (!etwasVorhanden) {
            Label leer = new Label("Du hast nichts zum Verkaufen.");
            root.getChildren().add(leer);
        }

        Scene scene = new Scene(root, 320, 400);
        stage.setScene(scene);
        stage.show();
    }
}