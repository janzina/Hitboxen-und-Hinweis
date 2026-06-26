package bta.ahaus.test.tutorial;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DifficultyWindow {

    public void show() {

        Stage stage = new Stage();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Button leicht =
                new Button("🟢 Leicht");

        Button mittel =
                new Button("🟡 Mittel");

        Button schwer =
                new Button("🔴 Schwer");

        leicht.setPrefWidth(250);
        mittel.setPrefWidth(250);
        schwer.setPrefWidth(250);

        leicht.setOnAction(e -> {

            stage.close();

            new SortierSpielWindow(
                    Difficulty.LEICHT
            ).show();
        });

        mittel.setOnAction(e -> {

            stage.close();

            new SortierSpielWindow(
                    Difficulty.MITTEL
            ).show();
        });

        schwer.setOnAction(e -> {

            stage.close();

            new SortierSpielWindow(
                    Difficulty.SCHWER
            ).show();
        });

        root.getChildren().addAll(
                leicht,
                mittel,
                schwer
        );

        Scene scene =
                new Scene(root, 300, 220);

        stage.setScene(scene);
        stage.setTitle("Schwierigkeit wählen");
        stage.show();
    }
}