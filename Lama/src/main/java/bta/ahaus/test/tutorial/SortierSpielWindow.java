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
        
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom,#efe6d2,#d9c4a3);"
        );

        titel = new Label(
                "📦 Ernte nach Gewicht sortieren"
        );

        titel.setFont(Font.font(28));
        titel.setLayoutX(320);
        titel.setLayoutY(20);

        root.getChildren().add(titel);

        createWaage(root);
        createWaagenTisch(root);
        createAnleitung(root);
        createTisch(root);
        createSortierPlaetze(root);
        createRandomKisten(root);

        Button pruefen =
                new Button("Prüfen");

        pruefen.setPrefWidth(180);
        pruefen.setPrefHeight(50);

        pruefen.setLayoutX(930);
        pruefen.setLayoutY(500);

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

        kisten.add(karte);

        root.getChildren().add(karte);
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

        for (int i = 1; i <= maxGewicht; i++) {

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

     for (int i = 0; i < anzahlKisten; i++) {

            int spalte = i / 4;
            int zeile = i % 4;

            createKiste(
                    root,
                    namen[i],
                    gewichte.get(i),
                    40 + spalte * 110,
                    110 + zeile * 90
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

        waage.setFitWidth(130);
        waage.setPreserveRatio(true);

        waage.setLayoutX(135);
        waage.setLayoutY(395);

        gewichtAnzeige = new Label(" ? kg");

        gewichtAnzeige.setFont(Font.font(18));

        gewichtAnzeige.setStyle(
                "-fx-background-color:white;" +
                "-fx-padding:8;" +
                "-fx-background-radius:8;" +
                "-fx-border-color:#8b5a2b;" +
                "-fx-border-radius:8;"
        );

        gewichtAnzeige.setLayoutX(110);
        gewichtAnzeige.setLayoutY(515);

        root.getChildren().addAll(
                waage,
                gewichtAnzeige
        );
    }
  
    private void createAnleitung(Pane root) {

        Label info = new Label(
                "📋 Aufgabe\n\n" +
                "1. Ziehe eine Kiste auf die Waage.\n\n" +
                "2. Merke dir das Gewicht.\n\n" +
                "3. Lege die Kiste auf einen Platz.\n\n" +
                "4. Sortiere die Kistne von links nach rechts\n" +
                "   (leicht → schwer).\n\n" +
                "5. Klicke auf Prüfen."
        );

        info.setStyle(
                "-fx-background-color: #d7b98e;" +
                "-fx-padding: 20;" +
                "-fx-border-color: #6b4226;" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 15;" +
                "-fx-border-radius: 15;" +
                "-fx-font-size: 16;" +
                "-fx-text-fill: #3e2723;"
        );

        info.setLayoutX(875);
        info.setLayoutY(60);

        root.getChildren().add(info);
    }
    
    private void createWaagenTisch(Pane root) {

        Rectangle platte = new Rectangle(
                130,
                560,
                120,
                45
        );

        platte.setStyle(
                "-fx-fill:#8b5a2b;"
        );

        Rectangle bein1 = new Rectangle(
                150,
                605,
                25,
                120
        );

        Rectangle bein2 = new Rectangle(
                235,
                605,
                25,
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
    
    private void createTisch(Pane root) {

        Rectangle platte =
                new Rectangle(
                        250,
                        560,
                        900,
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
                        1080,
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
    private void createSortierPlaetze(Pane root) {
        
        int anzahl = kistenAnzahl();

        double breite = 780.0 / anzahl;
        double startX = 315;

        for (int i = 0; i < anzahl; i++) {

            Rectangle platz =
                    new Rectangle(
                            startX + i * breite,
                            435,
                            70,
                            70
                    );

            platz.setArcWidth(15);
            platz.setArcHeight(15);

            platz.setStyle(
                    "-fx-fill: rgba(255,255,255,0.45);" +
                    "-fx-stroke:#8b5a2b;" +
                    "-fx-stroke-width:3;"
            );

            root.getChildren().add(platz);
        }
    }
    
    private int kistenAnzahl() {

        switch (difficulty) {

            case LEICHT:
                return 4;

            case MITTEL:
                return 6;

            case SCHWER:
                return 8;

            default:
                return 4;
        }
    }
    
    private void enableDrag(KistenKarte karte) {

        karte.setOnMouseDragged(e -> {

            karte.setLayoutX(e.getSceneX() - 45);
            karte.setLayoutY(e.getSceneY() - 35);

        });

        karte.setOnMouseReleased(e -> {

            if (karte.getBoundsInParent().intersects(
                    waage.getBoundsInParent())) {

                gewichtAnzeige.setText(
                        "Gewicht: "
                                + karte.getGewicht()
                                + " kg");
            }

            double startX = 315;
            double breite = 780.0 / kistenAnzahl();

            if (karte.getLayoutY() > 430) {

                int slot = (int) Math.round(
                        (karte.getLayoutX() - startX) / breite);

                if (slot >= 0 &&
                        slot < kistenAnzahl()) {

                    karte.setLayoutX(
                            startX + slot * breite + 5);

                    karte.setLayoutY(465);
                }
            }
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