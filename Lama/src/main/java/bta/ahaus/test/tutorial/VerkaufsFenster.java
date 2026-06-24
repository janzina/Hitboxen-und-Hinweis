package bta.ahaus.test.tutorial;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VerkaufsFenster {

    public static void open(Inventory inventory) {

        Stage stage = new Stage();
        stage.setTitle("Kasse");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        for (PlantType type : PlantType.values()) {
            int anzahl = inventory.getHarvestCount(type);
            if (anzahl <= 0)
                continue;

            Button verkaufen = new Button(
                    type.emoji + " "
                    + type.displayName
                    + " (" + anzahl + ") verkaufen"
            );

            verkaufen.setPrefWidth(250);

            verkaufen.setOnAction(e -> {
                if (inventory.removeHarvest(type, 1)) {
                    int preis = type.seedCost * 2;
                    inventory.addCoins(preis);
                    stage.close();
                    open(inventory);
                }
            });

            root.getChildren().add(verkaufen);
        }

        Scene scene = new Scene(root, 300, 400);

        stage.setScene(scene);
        stage.show();
    }
}