package bta.ahaus.test.tutorial;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MinispielAuswahlWindow {

    private final Inventory inventory;

    public MinispielAuswahlWindow(Inventory inventory) {
        this.inventory = inventory;
    }

    public void show() {

        Stage stage = new Stage();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Button sortierlager =
                new Button("📦 Sortierlager");
        
          sortierlager.setOnAction(e -> {
            new DifficultyWindow().show();

        });
        

        Button bald =
                new Button("🔒 Weitere Spiele folgen");

        sortierlager.setPrefWidth(250);
        
        bald.setPrefWidth(250);
        
     

        root.getChildren().addAll(
                sortierlager,
                bald
        );

        Scene scene =
                new Scene(root, 350, 250);

        stage.setTitle("Minispiele");
        stage.setScene(scene);
        stage.show();
    }
}