package bta.ahaus.lamaDrama.view.minigame;
 
import bta.ahaus.lamaDrama.model.data.MVerwaltung;
import bta.ahaus.lamaDrama.model.data.PlayerStats;
import bta.ahaus.lamaDrama.model.entity.Difficulty;
import static bta.ahaus.lamaDrama.model.entity.Difficulty.LEICHT;
import static bta.ahaus.lamaDrama.model.entity.Difficulty.MITTEL;
import static bta.ahaus.lamaDrama.model.entity.Difficulty.SCHWER;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
 
public class SortierSpielWindow {
    private List<KistenKarte> kisten = new ArrayList<>();
    private Difficulty difficulty;
    private Label     titel;
    private Label     gewichtAnzeige;
    private ImageView waage;
    private Stage     stage;
    private Button    pruefen;
    private Button    nochmal;
    private Button    verlassen;
 
    public SortierSpielWindow(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
 
    // Fenster öffnen und alles aufbauen
    public void show() {
 
        stage = new Stage();
 
        Pane root = new Pane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #efe6d2, #d9c4a3);");
 
        // Titel oben mittig
        titel = new Label("📦 Ernte nach Gewicht sortieren");
        titel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titel.setLayoutX(270);
        titel.setLayoutY(18);
        root.getChildren().add(titel);
 
        // Reihenfolge wichtig: Tische zuerst, dann Objekte darauf
        createWaagenTisch(root);
        createTisch(root);
        createWaage(root);
        createAnleitung(root);
        createButtons(root);
        createSortierPlaetze(root);
        createRandomKisten(root);
 
        stage.setScene(new Scene(root, 1200, 700));
        stage.setTitle("Sortierstation");
        stage.show();
    }
 
    // Waage links neben dem Sortiertisch
    private void createWaage(Pane root) {
 
        waage = new ImageView(new Image(
            getClass().getResourceAsStream("/assets/textures/minigames/Waage.png")
        ));
        waage.setFitWidth(165);
        waage.setPreserveRatio(true);
        waage.setLayoutX(110);
        waage.setLayoutY(390);
 
        // Gewichtsanzeige direkt unter der Waage
        gewichtAnzeige = new Label("? kg");
        gewichtAnzeige.setFont(Font.font("System", FontWeight.BOLD, 16));
        gewichtAnzeige.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 5 14;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #8b5a2b;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 2;"
        );
        gewichtAnzeige.setLayoutX(148);
        gewichtAnzeige.setLayoutY(525);
 
        root.getChildren().addAll(waage, gewichtAnzeige);
    }
 
    // Anleitungskasten oben rechts
    private void createAnleitung(Pane root) {
 
        Label info = new Label(
            "📋 Aufgabe\n\n" +
            "1. Ziehe eine Kiste auf die Waage.\n\n" +
            "2. Merke dir das Gewicht.\n\n" +
            "3. Lege die Kiste auf einen Platz.\n\n" +
            "4. Sortiere die Kisten von links\n" +
            "   nach rechts (leicht → schwer).\n\n" +
            "5. Klicke auf Prüfen."
        );
        info.setStyle(
            "-fx-background-color: #d7b98e;" +
            "-fx-padding: 16;" +
            "-fx-border-color: #6b4226;" +
            "-fx-border-width: 3;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-font-size: 14;" +
            "-fx-text-fill: #3e2723;"
        );
        info.setLayoutX(890);
        info.setLayoutY(55);
        root.getChildren().add(info);
    }
 
    // Kleiner Tisch unter der Waage (links)
    private void createWaagenTisch(Pane root) {
 
        Rectangle platte = new Rectangle(120, 560, 140, 40);
        platte.setStyle("-fx-fill: #8b5a2b;");
 
        Rectangle bein1 = new Rectangle(140, 600, 25, 100);
        Rectangle bein2 = new Rectangle(210, 600, 25, 100);
        bein1.setStyle("-fx-fill: #6b4226;");
        bein2.setStyle("-fx-fill: #6b4226;");
 
        root.getChildren().addAll(platte, bein1, bein2);
    }
 
    // Großer Sortiertisch (Mitte) – endet bei x=870, Buttons haben Platz rechts davon
    private void createTisch(Pane root) {
 
        // Tisch geht von x=270 bis x=870 (600px breit)
        Rectangle platte = new Rectangle(270, 560, 600, 40);
        platte.setStyle("-fx-fill: linear-gradient(to bottom, #a06a42, #7a4c2f);");
 
        Rectangle bein1 = new Rectangle(300, 600, 30, 100);
        Rectangle bein2 = new Rectangle(810, 600, 30, 100);
        bein1.setStyle("-fx-fill: #6b4226;");
        bein2.setStyle("-fx-fill: #6b4226;");
 
        root.getChildren().addAll(platte, bein1, bein2);
    }
 
    // Leere Felder auf dem Tisch wo man die Kisten hinlegen kann
    // Tisch: x=270 bis x=870 → 600px Breite für die Felder
    private void createSortierPlaetze(Pane root) {
 
        int anzahl = kistenAnzahl();
 
        // Jedes Feld passt genau zu einer Kiste (90px breit, 75px hoch + 8px Luft)
        double feldBreite = 100;
        double feldHoehe  = 85;
        double startX     = 275;   // kurz nach dem Tischrand
        double bereich    = 585;   // Tischbreite minus etwas Rand
        double abstand    = (bereich - anzahl * feldBreite) / (anzahl + 1);
        double feldY      = 560 - feldHoehe - 2; // direkt auf dem Tisch
 
        for (int i = 0; i < anzahl; i++) {
 
            double x = startX + abstand + i * (feldBreite + abstand);
 
            Rectangle feld = new Rectangle(x, feldY, feldBreite, feldHoehe);
            feld.setArcWidth(12);
            feld.setArcHeight(12);
            feld.setStyle(
                "-fx-fill: rgba(255,255,255,0.35);" +
                "-fx-stroke: #8b5a2b;" +
                "-fx-stroke-width: 2;"
            );
 
            // Kleine Zahl unten im Feld
            Label nr = new Label(String.valueOf(i + 1));
            nr.setFont(Font.font("System", FontWeight.BOLD, 10));
            nr.setStyle("-fx-text-fill: rgba(90,55,20,0.50);");
            nr.setLayoutX(x + feldBreite / 2 - 4);
            nr.setLayoutY(feldY + feldHoehe - 16);
 
            root.getChildren().addAll(feld, nr);
        }
    }
 
    // Buttons rechts neben dem Tisch (x=900, also klar außerhalb des Tisches)
    private void createButtons(Pane root) {
 
        // Prüfen – immer sichtbar, oben
        pruefen = new Button("✅ Prüfen");
        pruefen.setPrefWidth(170);
        pruefen.setPrefHeight(44);
        pruefen.setLayoutX(900);
        pruefen.setLayoutY(470);
        pruefen.setStyle(
            "-fx-background-color: #5a8a3c; -fx-text-fill: white;" +
            "-fx-font-size: 15; -fx-font-weight: bold;" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        );
        pruefen.setOnAction(e -> onPruefen());
        root.getChildren().add(pruefen);
 
        // Nochmal – erst nach Gewinn sichtbar
        nochmal = new Button("🔄 Nochmal spielen");
        nochmal.setPrefWidth(170);
        nochmal.setPrefHeight(44);
        nochmal.setLayoutX(900);
        nochmal.setLayoutY(525);
        nochmal.setStyle(
            "-fx-background-color: #8b5a2b; -fx-text-fill: white;" +
            "-fx-font-size: 15; -fx-font-weight: bold;" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        );
        nochmal.setVisible(false);
        nochmal.setOnAction(e -> {
            stage.close();
            new SortierSpielWindow(difficulty).show();
        });
        root.getChildren().add(nochmal);
 
        // Zurück – erst nach Gewinn sichtbar
        verlassen = new Button("🚪 Zurück");
        verlassen.setPrefWidth(170);
        verlassen.setPrefHeight(44);
        verlassen.setLayoutX(900);
        verlassen.setLayoutY(580);
        verlassen.setStyle(
            "-fx-background-color: #6b4226; -fx-text-fill: white;" +
            "-fx-font-size: 15; -fx-font-weight: bold;" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        );
        verlassen.setVisible(false);
        verlassen.setOnAction(e -> stage.close());
        root.getChildren().add(verlassen);
    }
 
    // Kisten zufällig erstellen je nach Schwierigkeitsgrad
    private void createRandomKisten(Pane root) {
 
        int maxGewicht;
        int anzahlKisten;
 
        switch (difficulty) {
            case LEICHT: maxGewicht = 10; anzahlKisten = 4; break;
            case MITTEL: maxGewicht = 20; anzahlKisten = 6; break;
            case SCHWER: maxGewicht = 30; anzahlKisten = 8; break;
            default:     maxGewicht = 10; anzahlKisten = 4;
        }
 
        // Zufällige Gewichte aus dem Bereich 1 bis maxGewicht
        List<Integer> gewichte = new ArrayList<>();
        for (int i = 1; i <= maxGewicht; i++) gewichte.add(i);
        Collections.shuffle(gewichte);
 
        String[] namen = {
            "Karotten", "Tomaten", "Kartoffeln", "Weizen",
            "Kürbis", "Äpfel", "Mais", "Erdbeeren"
        };
 
        // Kisten in 2 Spalten links aufstellen (Bereich x=0..260)
        for (int i = 0; i < anzahlKisten; i++) {
            int spalte = i / 4;
            int zeile  = i % 4;
 
            KistenKarte karte = new KistenKarte(namen[i], gewichte.get(i));
            karte.setLayoutX(20 + spalte * 120);
            karte.setLayoutY(80 + zeile * 110);
            enableDrag(karte);
            kisten.add(karte);
            root.getChildren().add(karte);
        }
    }
 
    // Macht eine Kiste mit der Maus verschiebbar
    private void enableDrag(KistenKarte karte) {
 
        // Kiste folgt der Maus (Mitte der Kiste am Mauszeiger)
        karte.setOnMouseDragged(e -> {
            karte.setLayoutX(e.getSceneX() - 45);
            karte.setLayoutY(e.getSceneY() - 37);
        });
 
        // Wenn Maustaste losgelassen wird
        karte.setOnMouseReleased(e -> {
 
            // Gewicht anzeigen wenn Kiste auf der Waage liegt
            if (karte.getBoundsInParent().intersects(waage.getBoundsInParent())) {
                gewichtAnzeige.setText("⚖ " + karte.getGewicht() + " kg");
            }
 
            // Einrasten in das nächste Feld wenn Kiste im Tischbereich losgelassen wird
            double feldY = 560 - 85 - 2;
            if (karte.getLayoutY() >= feldY - 50) {
 
                int    anzahl    = kistenAnzahl();
                double feldBreite = 100;
                double startX     = 275;
                double bereich    = 585;
                double abstand    = (bereich - anzahl * feldBreite) / (anzahl + 1);
 
                // Welches Feld ist am nächsten?
                double kisteMitte   = karte.getLayoutX() + 45;
                int    naechstesFeld = 0;
                double minAbstand    = Double.MAX_VALUE;
 
                for (int i = 0; i < anzahl; i++) {
                    double feldMitte = startX + abstand + i * (feldBreite + abstand) + feldBreite / 2;
                    double dist = Math.abs(kisteMitte - feldMitte);
                    if (dist < minAbstand) {
                        minAbstand    = dist;
                        naechstesFeld = i;
                    }
                }
 
                // Nur einrasten wenn nah genug am Feld
                if (minAbstand < feldBreite) {
                    double feldX = startX + abstand + naechstesFeld * (feldBreite + abstand);
                    karte.setLayoutX(feldX + 5);
                    karte.setLayoutY(feldY + 5);
                }
            }
        });
    }
 
    // Wird aufgerufen wenn man auf Prüfen klickt
    private void onPruefen() {
 
        // Alle Kisten müssen auf dem Tisch liegen, nicht noch links
        double feldY = 560 - 85 - 2;
        for (KistenKarte k : kisten) {
            if (k.getLayoutY() < feldY - 10) {
                titel.setText("⚠️ Lege zuerst alle Kisten auf den Tisch!");
                return;
            }
        }
 
        if (checkOrder()) {
            rewardPlayer();
        } else {
            titel.setText("❌ Noch nicht richtig – leicht → schwer!");
        }
    }
 
    // Prüft ob die Kisten richtig sortiert sind.
    // Dazu sortieren wir die Kisten per Merge Sort nach Gewicht
    // und vergleichen das Ergebnis mit der Reihenfolge auf dem Tisch.
    private boolean checkOrder() {
 
        // Schritt 1: Kisten von links nach rechts aufnehmen
        List<KistenKarte> aufDemTisch = new ArrayList<>(kisten);
        aufDemTisch.sort(Comparator.comparingDouble(KistenKarte::getLayoutX));
 
        // Schritt 2: dieselben Kisten per Merge Sort nach Gewicht sortieren
        List<KistenKarte> richtigeReihenfolge = mergeSort(new ArrayList<>(aufDemTisch));
 
        // Schritt 3: stimmt die Tisch-Reihenfolge mit der richtigen überein?
        for (int i = 0; i < aufDemTisch.size(); i++) {
            if (aufDemTisch.get(i).getGewicht() != richtigeReihenfolge.get(i).getGewicht()) {
                return false; // Kiste ist falsch platziert
            }
        }
        return true; // Alle Kisten sind richtig!
    }
 
    // -----------------------------------------------------------------------
    // MERGE SORT
    //
    // So funktioniert es:
    //   1. Teile die Liste in eine linke und eine rechte Hälfte
    //   2. Sortiere jede Hälfte (die Methode ruft sich selbst auf)
    //   3. Füge die zwei sortierten Hälften zusammen
    //
    // Beispiel: [3, 1, 4, 2]
    //   Teilen  →  [3, 1]  und  [4, 2]
    //   Teilen  →  [3] [1]      [4] [2]
    //   Zusammen→  [1, 3]       [2, 4]
    //   Zusammen→  [1, 2, 3, 4]  ✅
    // -----------------------------------------------------------------------
 
    // Teilt die Liste auf und sortiert sie
    private List<KistenKarte> mergeSort(List<KistenKarte> liste) {
 
        // Eine Liste mit nur 1 Element ist bereits sortiert
        if (liste.size() <= 1) {
            return liste;
        }
 
        // Liste in zwei Hälften teilen
        int mitte = liste.size() / 2;
        List<KistenKarte> links  = new ArrayList<>(liste.subList(0, mitte));
        List<KistenKarte> rechts = new ArrayList<>(liste.subList(mitte, liste.size()));
 
        // Jede Hälfte sortieren und dann zusammenführen
        return zusammenfuehren(mergeSort(links), mergeSort(rechts));
    }
 
    // Nimmt zwei sortierte Listen und fügt sie zu einer sortierten Liste zusammen.
    // Immer die leichtere Kiste zuerst nehmen.
    private List<KistenKarte> zusammenfuehren(List<KistenKarte> links, List<KistenKarte> rechts) {
 
        List<KistenKarte> ergebnis = new ArrayList<>();
        int l = 0; // zeigt auf die aktuelle Kiste in der linken Liste
        int r = 0; // zeigt auf die aktuelle Kiste in der rechten Liste
 
        // Solange beide Listen noch Kisten haben: die leichtere nehmen
        while (l < links.size() && r < rechts.size()) {
            if (links.get(l).getGewicht() <= rechts.get(r).getGewicht()) {
                ergebnis.add(links.get(l));
                l++;
            } else {
                ergebnis.add(rechts.get(r));
                r++;
            }
        }
 
        // Übrige Kisten der linken Liste hinzufügen
        while (l < links.size()) {
            ergebnis.add(links.get(l));
            l++;
        }
 
        // Übrige Kisten der rechten Liste hinzufügen
        while (r < rechts.size()) {
            ergebnis.add(rechts.get(r));
            r++;
        }
 
        return ergebnis;
    }
 
    // Belohnung vergeben wenn alles richtig sortiert ist
    private void rewardPlayer() {
 
        int xp;
        int coins;
 
        switch (difficulty) {
            case LEICHT: xp = 10; coins = 20; break;
            case MITTEL: xp = 20; coins = 35; break;
            case SCHWER: xp = 30; coins = 50; break;
            default:     xp = 10; coins = 20;
        }
 
        PlayerStats.getInstance().addXP(xp);
        MVerwaltung.getInstance().getInventory().addCoins(coins);
 
        titel.setText("✅ Geschafft! +" + xp + " XP  |  +" + coins + " Münzen 🎉");
 
        // Prüfen sperren damit Belohnung nicht doppelt vergeben wird
        pruefen.setDisable(true);
        nochmal.setVisible(true);
        verlassen.setVisible(true);
    }
 
    // Gibt zurück wie viele Kisten es gibt
    private int kistenAnzahl() {
        switch (difficulty) {
            case LEICHT: return 4;
            case MITTEL: return 6;
            case SCHWER: return 8;
            default:     return 4;
        }
    }
}