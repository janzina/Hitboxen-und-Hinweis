package bta.ahaus.test.tutorial;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ShopMinispiel {

    public static void open(Inventory inventory) {

        Stage stage = new Stage();
        stage.setTitle("Hofladen");

        BorderPane root = new BorderPane();
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom,#f4deb3,#c89b6d);"
        );

        Label titel = new Label("🏪 Hofladen");
        titel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        root.setTop(titel);
        BorderPane.setAlignment(titel, Pos.CENTER);

        Pane mitte = new Pane();

        Label meldung = new Label();    
        meldung.setFont(Font.font(18));
        meldung.setLayoutX(350);
        meldung.setLayoutY(10);
       

       ImageView regal = new ImageView(
            new Image(
                ShopMinispiel.class.getResourceAsStream(
                    "/assets/textures/minigames/holz_regal.png"
                )
            )
        );

        regal.setFitWidth(1120);
        regal.setPreserveRatio(true);

        regal.setLayoutX(40);
        regal.setLayoutY(-20);
        
        ImageView apfel = new ImageView(
                new Image(
                        ShopMinispiel.class.getResourceAsStream(
                                "/assets/textures/minigames/apfel.png"
                        )
                )
        );
        
        apfel.setFitWidth(70);
        apfel.setPreserveRatio(true);
        apfel.setLayoutX(140);
        apfel.setLayoutY(20);

        Label apfelPreis =
                new Label("🍎\n3 Münzen\n30 XP");
        apfelPreis.setLayoutX(130);
        apfelPreis.setLayoutY(90);

        ImageView schoko = new ImageView(
                new Image(
                        ShopMinispiel.class.getResourceAsStream(
                                "/assets/textures/minigames/schokolade.png"
                        )
                )
        );
        
        schoko.setFitWidth(70);
        schoko.setPreserveRatio(true);
        schoko.setLayoutX(360);
        schoko.setLayoutY(20);

        Label schokoPreis =
                new Label("🍫\n5 Münzen\n50 XP");
        schokoPreis.setLayoutX(350);
        schokoPreis.setLayoutY(90);

        ImageView kraeuter = new ImageView(
                new Image(
                        ShopMinispiel.class.getResourceAsStream(
                                "/assets/textures/minigames/kraeutermix.png"
                        )
                )
        );
        
        kraeuter.setFitWidth(70);
        kraeuter.setPreserveRatio(true);
        kraeuter.setLayoutX(580);
        kraeuter.setLayoutY(20);

        Label kraeuterPreis =
                new Label("🌿\n8 Münzen\n80 XP");
        kraeuterPreis.setLayoutX(570);
        kraeuterPreis.setLayoutY(90);

        ImageView honig = new ImageView(
                new Image(
                        ShopMinispiel.class.getResourceAsStream(
                                "/assets/textures/minigames/honig.png"
                        )
                )
        );
        honig.setFitWidth(70);
        honig.setPreserveRatio(true);
        honig.setLayoutX(800);
        honig.setLayoutY(20);

        Label honigPreis =
                new Label("🍯\n12 Münzen\n100 XP");
        honigPreis.setLayoutX(790);
        honigPreis.setLayoutY(90);

        
        var karotte = SeedIcon.create(
                PlantType.KAROTTE,80);
        karotte.setLayoutX(120);
        karotte.setLayoutY(300);

        Label karottePreis =
                new Label("0 Münzen");
        karottePreis.setLayoutX(125);
        karottePreis.setLayoutY(380);

        var kartoffel = SeedIcon.create(
                PlantType.KARTOFFEL,80);
        kartoffel.setLayoutX(310);
        kartoffel.setLayoutY(300);

        Label kartoffelPreis =
                new Label("5 Münzen");
        kartoffelPreis.setLayoutX(315);
        kartoffelPreis.setLayoutY(380);

        var tomate = SeedIcon.create(
                PlantType.TOMATE,80);
        tomate.setLayoutX(500);
        tomate.setLayoutY(300);

        Label tomatePreis =
                new Label("15 Münzen");
        tomatePreis.setLayoutX(505);
        tomatePreis.setLayoutY(380);

        var weizen = SeedIcon.create(
                PlantType.WEIZEN,80);
        weizen.setLayoutX(700);
        weizen.setLayoutY(300);

        Label weizenPreis =
                new Label("3 Münzen");
        weizenPreis.setLayoutX(705);
        weizenPreis.setLayoutY(380);
        
        var kohl = SeedIcon.create(
               PlantType.KOHL, 80);
        kohl.setLayoutX(810);
        kohl.setLayoutY(300);
       
        Label kohlPreis =
                new Label("10 Münzen");

        kohlPreis.setLayoutX(815);
        kohlPreis.setLayoutY(380);

        var kuerbis = SeedIcon.create(
                PlantType.KUERBIS,80);
        kuerbis.setLayoutX(995);
        kuerbis.setLayoutY(300);

        Label kuerbisPreis =
                new Label("25 Münzen");
        kuerbisPreis.setLayoutX(1000);
        kuerbisPreis.setLayoutY(380);

        ImageView kasse = new ImageView(
                new Image(
                        ShopMinispiel.class.getResourceAsStream(
                                "/assets/textures/minigames/Kasse.png"
                        )
                )
        );

        kasse.setFitWidth(700);
        kasse.setPreserveRatio(true);

        kasse.setLayoutX(-300);
        kasse.setLayoutY(300);
 

        kasse.setOnMouseClicked(e ->
                VerkaufsFenster.open(inventory));
        
        apfel.setOnMouseClicked(e ->
                kaufeSpezialitaet(
                        inventory,
                        PlantType.APFEL,
                        meldung
                ));

        schoko.setOnMouseClicked(e ->
                kaufeSpezialitaet(
                        inventory,
                        PlantType.SCHOKOLADE,
                        meldung
                ));

        kraeuter.setOnMouseClicked(e ->
                kaufeSpezialitaet(
                        inventory,
                        PlantType.KRAEUTERMIX,
                        meldung
                ));

        honig.setOnMouseClicked(e ->
                kaufeSpezialitaet(
                        inventory,
                        PlantType.HONIG,
                        meldung
                ));

        karotte.setOnMouseClicked(e ->
                kaufeSaatgut(
                        inventory,
                        PlantType.KAROTTE,
                        meldung
                ));

        kartoffel.setOnMouseClicked(e ->
                kaufeSaatgut(
                        inventory,
                        PlantType.KARTOFFEL,
                        meldung
                ));

        tomate.setOnMouseClicked(e ->
                kaufeSaatgut(
                        inventory,
                        PlantType.TOMATE,
                        meldung
                ));

        weizen.setOnMouseClicked(e ->
                kaufeSaatgut(
                        inventory,
                        PlantType.WEIZEN,
                        meldung
                ));
        
        kohl.setOnMouseClicked(e ->
                kaufeSaatgut(
                        inventory,
                        PlantType.KOHL,
                        meldung
                ));

        kuerbis.setOnMouseClicked(e ->
                kaufeSaatgut(
                        inventory,
                        PlantType.KUERBIS,
                        meldung
                ));
                

        mitte.getChildren().addAll(
                regal,
                meldung,
                apfel,
                schoko,
                kraeuter,
                honig,
                apfelPreis,
                schokoPreis,
                kraeuterPreis,
                honigPreis,
                karotte,
                kartoffel,
                tomate,
                weizen,
                kohl,
                kuerbis,
                karottePreis,
                kartoffelPreis,
                tomatePreis,
                weizenPreis,
                kohlPreis,
                kuerbisPreis,
                kasse
        );

        root.setCenter(mitte);

        Scene scene = new Scene(root,1200,700);
        stage.setScene(scene);
        stage.show();
    }

    private static void kaufeSaatgut(
            Inventory inventory,
            PlantType type,
            Label meldung) {

        if (!inventory.spend(type.seedCost)) {
            meldung.setText("❌ Nicht genug Münzen!");
            return;
        }

        inventory.addSeed(type,1);

        meldung.setText(
                "✅ " +
                type.displayName +
                " gekauft!"
        );
    }

    private static void kaufeSpezialitaet(
            Inventory inventory,
            PlantType type,
            Label meldung) {

        if (PlayerStats.getInstance().getXP()
                < type.minXP) {

            meldung.setText(
                    "❌ Du brauchst "
                            + type.minXP
                            + " XP!"
            );
            return;
        }

        if (!inventory.spend(type.seedCost)) {

            meldung.setText(
                    "❌ Nicht genug Münzen!"
            );
            return;
        }

        inventory.addSeed(type,1);

        meldung.setText(
                "✅ "
                        + type.displayName
                        + " gekauft!"
        );
    }
}