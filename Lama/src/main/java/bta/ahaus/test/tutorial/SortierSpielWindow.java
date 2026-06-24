package bta.ahaus.test.tutorial;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortierSpielWindow {

    private final List<KistenKarte> kisten =
            new ArrayList<>();

    private final Difficulty difficulty;

    private Label gewichtAnzeige;
    private Label titel;
    private Stage stage;
    private ImageView waage;
    private Button verlassen;
    private Button nochmal;
    
    public SortierSpielWindow(
            Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void show() {

        stage = new Stage();

        Pane root = new Pane();

        ImageView background = new ImageView(
                new Image(
                        getClass().getResourceAsStream(
                                "/assets/textures/minigames/Lagerhalle.png"
                        )
                )
        );

        background.setFitWidth(1200);
        background.setFitHeight(700);

        root.getChildren().add(background);

        titel = new Label(
                "📦 Ernte nach Gewicht sortieren"
        );

        titel.setFont(Font.font(28));
        titel.setLayoutX(320);
        titel.setLayoutY(20);

        root.getChildren().add(titel);

        createWaage(root);
        createAnleitung(root);
        createTisch(root);
        createRegal(root);

        createRandomKisten(root);

        Button pruefen =
                new Button("Prüfen");

        pruefen.setPrefWidth(180);
        pruefen.setPrefHeight(50);

        pruefen.setLayoutX(930);
        pruefen.setLayoutY(590);

        pruefen.setOnAction(e -> {

            if (checkOrder()) {

                rewardPlayer();

            } else {

                titel.setText(
                        "❌ Noch nicht richtig!"
                );
            }
        });

        root.getChildren().add(pruefen);
        
        verlassen = new Button("Zurück");

        verlassen.setLayoutX(930);
        verlassen.setLayoutY(530);

        verlassen.setVisible(false);

        verlassen.setOnAction(e ->
                stage.close()
        );

        root.getChildren().add(verlassen);

        Scene scene =
                new Scene(root, 1200, 700);

        stage.setScene(scene);
        stage.setTitle("Sortierstation");
        stage.show();
        
        nochmal = new Button("Nochmal spielen");

        nochmal.setLayoutX(930);
        nochmal.setLayoutY(470);

        nochmal.setPrefWidth(180);

        nochmal.setVisible(false);

        nochmal.setOnAction(e -> {

            stage.close();

            new SortierSpielWindow(
                    difficulty
            ).show();
        });

        root.getChildren().add(nochmal);
       
    }

    private void createRandomKisten(Pane root) {
        int maxGewicht;
        int anzahlKisten;

        switch (difficulty) {

            case LEICHT:
                maxGewicht = 10;
                anzahlKisten = 4;
                break;

            case MITTEL:
                maxGewicht = 20;
                anzahlKisten = 6;
                break;

            case SCHWER:
                maxGewicht = 30;
                anzahlKisten = 8;
                break;

            default:
                maxGewicht = 10;
                anzahlKisten = 4;
        }

        List<Integer> gewichte =
                new ArrayList<>();

        for (int i = 1;
             i <= maxGewicht;
             i++) {

            gewichte.add(i);
        }

        Collections.shuffle(gewichte);

        String[] namen = {
                "Karotten",
                "Tomaten",
                "Kartoffeln",
                "Weizen",
                "Kürbis",
                "Äpfel",
                "Mais",
                "Erdbeeren"
        };

        for (int i = 0;
             i < anzahlKisten;
             i++) {

            int spalte = i / 3;
            int zeile = i % 3;

            createKiste(
                    root,
                    namen[i],
                    gewichte.get(i),
                    40 + spalte * 140,
                    430 + zeile * 70
            );
        }
    }
            
    private void createWaage(Pane root) {

        waage = new ImageView(
                new Image(
                        getClass().getResourceAsStream(
                                "/assets/textures/minigames/Waage.png"
                        )
                )
        );

        waage.setFitWidth(250);
        waage.setPreserveRatio(true);

        waage.setLayoutX(120);
        waage.setLayoutY(180);

        gewichtAnzeige =
                new Label("⚖️ ? kg");

        gewichtAnzeige.setFont(
                Font.font(28)
        );

        gewichtAnzeige.setStyle(
                "-fx-background-color: rgba(255,255,255,0.9);" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 10;"
        );

        gewichtAnzeige.setLayoutX(120);
        gewichtAnzeige.setLayoutY(450);

        root.getChildren().addAll(
                waage,
                gewichtAnzeige
        );
    }
  
    private void createAnleitung(Pane root) {

        Label info = new Label(
                "1. Kiste anklicken\n\n" +
                "2. Gewicht merken\n\n" +
                "3. Auf den Tisch ziehen\n\n" +
                "4. Von links nach rechts\n" +
                "   sortieren"
        );

        info.setStyle(
                "-fx-background-color: rgba(255,255,255,0.92);" +
                "-fx-padding: 20;" +
                "-fx-border-color: saddlebrown;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-font-size: 18;"
        );

        info.setLayoutX(900);
        info.setLayoutY(170);

        root.getChildren().add(info);
    }

    private void createTisch(Pane root) {

        Rectangle platte =
                new Rectangle(
                        250,
                        560,
                        720,
                        45
                );

        platte.setStyle(
                "-fx-fill: linear-gradient(to bottom,#a06a42,#7a4c2f);"
        );

        Rectangle bein1 =
                new Rectangle(
                        280,
                        605,
                        35,
                        120
                );

        Rectangle bein2 =
                new Rectangle(
                        900,
                        605,
                        35,
                        120
                );

        bein1.setStyle("-fx-fill:#6b4226;");
        bein2.setStyle("-fx-fill:#6b4226;");

        root.getChildren().addAll(
                platte,
                bein1,
                bein2
        );
    }

    private void createKiste(
            Pane root,
            String name,
            int gewicht,
            double x,
            double y
    ) {

        KistenKarte karte =
                new KistenKarte(
                        name,
                        gewicht
                );

        karte.setLayoutX(x);
        karte.setLayoutY(y);

        enableDrag(karte);

       karte.setOnMouseReleased(e -> {
        if (karte.getBoundsInParent()
                .intersects(
                        waage.getBoundsInParent()
                )) {

            gewichtAnzeige.setText(
                    "⚖️ " +
                    karte.getGewicht() +
                    " kg"
            );
        }
    });

        kisten.add(karte);

        root.getChildren().add(karte);
    }
    
    private void createRegal(Pane root) {

        Rectangle seiteLinks =
                new Rectangle(
                        20,
                        260,
                        25,
                        280
                );

        Rectangle seiteRechts =
                new Rectangle(
                        170,
                        260,
                        25,
                        280
                );

        Rectangle fach1 =
                new Rectangle(
                        20,
                        320,
                        175,
                        15
                );

        Rectangle fach2 =
                new Rectangle(
                        20,
                        410,
                        175,
                        15
                );

        Rectangle fach3 =
                new Rectangle(
                        20,
                        500,
                        175,
                        15
                );

        seiteLinks.setStyle("-fx-fill:#6b4226;");
        seiteRechts.setStyle("-fx-fill:#6b4226;");

        fach1.setStyle("-fx-fill:#8b5a2b;");
        fach2.setStyle("-fx-fill:#8b5a2b;");
        fach3.setStyle("-fx-fill:#8b5a2b;");

        root.getChildren().addAll(
                seiteLinks,
                seiteRechts,
                fach1,
                fach2,
                fach3
        );
    }

    private void enableDrag(
            KistenKarte karte
    ) {

        karte.setOnMouseDragged(e -> {

            karte.setLayoutX(
                    e.getSceneX() - 60
            );

            karte.setLayoutY(
                    e.getSceneY() - 40
            );
        });
    }

    private boolean checkOrder() {

        List<KistenKarte> aktuell =
                new ArrayList<>(kisten);

        aktuell.sort(
                Comparator.comparingDouble(
                        KistenKarte::getLayoutX
                )
        );

        for (int i = 0;
             i < aktuell.size() - 1;
             i++) {

            if (aktuell.get(i)
                    .getGewicht()
                    >
                    aktuell.get(i + 1)
                            .getGewicht()) {

                return false;
            }
        }

        return true;
    }

    private void rewardPlayer() {

        int xp;
        int coins;

        switch (difficulty) {

            case LEICHT:
                xp = 10;
                coins = 20;
                break;

            case MITTEL:
                xp = 20;
                coins = 35;
                break;

            case SCHWER:
                xp = 30;
                coins = 50;
                break;

            default:
                xp = 10;
                coins = 20;
        }

        PlayerStats.getInstance()
                .addXP(xp);

        MVerwaltung.getInstance()
                .getInventory()
                .addCoins(coins);

        titel.setText(
                "✅ Geschafft! +" +
                xp +
                " XP | +" +
                coins +
                " Münzen"
        );
        
        titel.setText(
        "✅ Geschafft! +"
                + xp
                + " XP | +"
                + coins
                + " Münzen"
        );

        verlassen.setVisible(true);
        nochmal.setVisible(true);
        
    }
    
    public void createVerlassen(){
        Button verlassen = new Button("Zurück");

        verlassen.setLayoutX(930);
        verlassen.setLayoutY(530);

        verlassen.setOnAction(e ->
            stage.close()
    );
    }
    
}