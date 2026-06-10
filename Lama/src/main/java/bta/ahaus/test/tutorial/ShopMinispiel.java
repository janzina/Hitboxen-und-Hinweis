package bta.ahaus.test.tutorial;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ShopMinispiel {

    public static void open(Inventory inventory) {
        Stage stage = new Stage();
        stage.setTitle("Shop");

        // ── Hintergrund ────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #87CEEB;");

        // ── Titel oben ─────────────────────────────────────────────────────────
       Label titel = new Label(" Willkommen im Shop ");
        titel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titel.setTextFill(Color.WHITE);
        titel.setPadding(new Insets(15));

        Label meldung = new Label("");
        meldung.setTextFill(Color.RED);
        meldung.setFont(Font.font(16));

        VBox oben = new VBox(5);
        oben.setAlignment(Pos.CENTER);
        oben.getChildren().addAll(titel, meldung);

        root.setTop(oben);
        
        // ── Ballon in der Mitte ────────────────────────────────────────────────
        ImageView ballon = new ImageView(new Image(
            ShopMinispiel.class.getResourceAsStream(
                "/assets/textures/minigames/ballon.png")));
        ballon.setFitWidth(120);
        ballon.setFitHeight(160);
        ballon.setPreserveRatio(true);
        ballon.setTranslateY(-300);
        
        ImageView shopStand = new ImageView(new Image(
            ShopMinispiel.class.getResourceAsStream(
                "/assets/textures/minigames/shop_stand.png")));
        shopStand.setFitWidth(200);
        shopStand.setPreserveRatio(true);
        
        
        Pane mitte = new Pane();

        // Regal größer
        shopStand.setFitWidth(450);
        shopStand.setLayoutX(180);
        shopStand.setLayoutY(120);

        // Ballon
        ballon.setLayoutX(360);
        ballon.setLayoutY(0);

        // Produkte auf dem Regal
        Label apfel = new Label("🍎");
        apfel.setStyle("-fx-font-size: 40px;");
        apfel.setLayoutX(340);
        apfel.setLayoutY(260);

        Label schoko = new Label("🍫");
        schoko.setStyle("-fx-font-size: 40px;");
        schoko.setLayoutX(460);
        schoko.setLayoutY(260);

        Label kraeuter = new Label("🌿");
        kraeuter.setStyle("-fx-font-size: 40px;");
        kraeuter.setLayoutX(340);
        kraeuter.setLayoutY(360);

        Label honig = new Label("🍯");
        honig.setStyle("-fx-font-size: 40px;");
        honig.setLayoutX(460);
        honig.setLayoutY(360);

        // Klick auf Apfel
        apfel.setOnMouseClicked(e -> {
            if (PlayerStats.getInstance().getXP() >= 30 &&
                inventory.spend(PlantType.APFEL.seedCost)) {

                inventory.addItem(PlantType.APFEL, 1);
                ballonLiefern(ballon);
            }
            if (PlayerStats.getInstance().getXP() < 30) {
            meldung.setText("❌ Du kannst das noch nicht kaufen!");
            return;
        }
            meldung.setText("✅ Gekauft!");
        });

        // Klick auf Schokolade
       schoko.setOnMouseClicked(e -> {
            if (PlayerStats.getInstance().getXP() < 50) {
                meldung.setText("❌ Du kannst das noch nicht kaufen!");
                return;
            }

            if (!inventory.spend(PlantType.SCHOKOLADE.seedCost)) {
                meldung.setText("💰 Du hast zu wenig Münzen!");
                return;
            }

            meldung.setText("✅ Schokolade gekauft!");

            inventory.addItem(PlantType.SCHOKOLADE, 1);
            ballonLiefern(ballon);
        });
       
        // Klick auf Kräutermix
        kraeuter.setOnMouseClicked(e -> {
            if (PlayerStats.getInstance().getXP() >= 80 &&
                inventory.spend(PlantType.KRAEUTERMIX.seedCost)) {

                inventory.addItem(PlantType.KRAEUTERMIX, 1);
                ballonLiefern(ballon);
            }
            if (PlayerStats.getInstance().getXP() < 80) {
            meldung.setText("❌ Du kannst das noch nicht kaufen!");
            return;
        }
            meldung.setText("✅ Gekauft!");
        });

        // Klick auf Honig
        honig.setOnMouseClicked(e -> {
            if (PlayerStats.getInstance().getXP() >= 100 &&
                inventory.spend(PlantType.HONIG.seedCost)) {

                inventory.addItem(PlantType.HONIG, 1);
                ballonLiefern(ballon);
            }   
            if (PlayerStats.getInstance().getXP() < 100) {
            meldung.setText("❌ Du kannst das noch nicht kaufen!");
            return;
        }
            meldung.setText("✅ Gekauft!");
        });

        mitte.getChildren().addAll(
            shopStand,
            ballon,
            apfel,
            schoko,
            kraeuter,
            honig
        );

        root.setCenter(mitte);

        // ── Unten: Münzen, XP, Schließen ──────────────────────────────────────
        HBox unten = new HBox(20);
        unten.setAlignment(Pos.CENTER);
        unten.setPadding(new Insets(10));
        unten.setStyle("-fx-background-color: rgba(0,0,0,0.4);");

        Label munzen = new Label("💰 " + inventory.getCoins() + " Münzen");
        munzen.setFont(Font.font(15));
        munzen.setTextFill(Color.GOLD);

        Label xp = new Label("✨ " + PlayerStats.getInstance().getXP() + " XP");
        xp.setFont(Font.font(15));
        xp.setTextFill(Color.LIGHTGREEN);

        Label schliessen = new Label("✖ Schließen");
        schliessen.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        schliessen.setTextFill(Color.WHITE);
        schliessen.setPadding(new Insets(5, 15, 5, 15));
        schliessen.setStyle("-fx-background-color: #cc4444; -fx-background-radius: 8;");
        schliessen.setOnMouseClicked(e -> stage.close());

        unten.getChildren().addAll(munzen, xp, schliessen);
        root.setBottom(unten);

        // Fenster anzeigen
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    // ── Ballon kommt von oben runter und fliegt wieder weg ────────────────────
 private static void ballonLiefern(ImageView ballon) {

    Timeline animation = new Timeline(

        // Start oben
        new KeyFrame(Duration.seconds(0),
            new KeyValue(ballon.translateYProperty(), -300)),

        // Beim Shop ankommen
        new KeyFrame(Duration.seconds(2),
            new KeyValue(ballon.translateYProperty(), 0)),

        // 2 Sekunden beim Shop stehen bleiben
        new KeyFrame(Duration.seconds(4),
            new KeyValue(ballon.translateYProperty(), 0)),

        // Dann wieder wegfliegen
        new KeyFrame(Duration.seconds(6),
            new KeyValue(ballon.translateYProperty(), -300))
    );

        animation.play();
    }

}