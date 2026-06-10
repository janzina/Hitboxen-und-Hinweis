package bta.ahaus.test.tutorial;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class PutzMinispiel {

    private static final int W = 800;
    private static final int H = 600;

    // ← Flecken-Positionen auf dem Lama anpassen
    private static final double[][] FLECKEN = {
        {370, 280}, {410, 310}, {350, 340},
        {390, 260}, {430, 290}, {360, 370},
        {400, 330}, {380, 250}, {420, 350}, {345, 300}
    };
    private static final double FLECK_RADIUS = 22;

    // ← Belohnung anpassen
    private static final int BELOHNUNG_XP    = 10;
    private static final int BELOHNUNG_COINS = 5;

    public static void open(Inventory inventory, LamaDreck lamaDreck) {
        Stage stage = new Stage();
        stage.setTitle("Lama putzen!");

        Pane root = new Pane();
        Canvas canvas = new Canvas(W, H);
        root.getChildren().add(canvas);
        root.setCursor(Cursor.NONE);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Bilder laden
        Image lama  = new Image(PutzMinispiel.class.getResourceAsStream(
                "/assets/textures/minigames/Lama_clean.png"));
        Image seife = new Image(PutzMinispiel.class.getResourceAsStream(
                "/assets/textures/minigames/seife.png"));

        // Flecken – alle starten als schmutzig
        List<boolean[]> flecken = new ArrayList<>();
        for (int i = 0; i < FLECKEN.length; i++) {
            flecken.add(new boolean[]{false});
        }

        double[]  mx     = {W / 2.0};
        double[]  my     = {H / 2.0};
        boolean[] fertig = {false};

        // Maus bewegt sich → Seife folgt
        canvas.setOnMouseMoved(e -> {
            mx[0] = e.getX();
            my[0] = e.getY();
            zeichne(gc, lama, seife, mx[0], my[0], flecken, fertig[0]);
        });

        // Maus gedrückt + bewegt → Flecken entfernen
        canvas.setOnMouseDragged(e -> {
            mx[0] = e.getX();
            my[0] = e.getY();

            if (!fertig[0]) {
                for (int i = 0; i < FLECKEN.length; i++) {
                    double dist = Math.sqrt(
                        Math.pow(mx[0] - FLECKEN[i][0], 2) +
                        Math.pow(my[0] - FLECKEN[i][1], 2)
                    );
                    if (dist < FLECK_RADIUS) {
                        flecken.get(i)[0] = true;
                    }
                }

                // Alle Flecken weg → fertig!
                boolean alleSauber = flecken.stream().allMatch(f -> f[0]);
                if (alleSauber) {
                    fertig[0] = true;
                    inventory.addCoins(BELOHNUNG_COINS);
                    PlayerStats.getInstance().addXP(BELOHNUNG_XP);
                    lamaDreck.reset();
                }
            }
            zeichne(gc, lama, seife, mx[0], my[0], flecken, fertig[0]);
        });

        // Klick auf "Zurück zur Farm" Button
        canvas.setOnMouseClicked(e -> {
            if (fertig[0]) {
                double btnX = 250, btnY = 330, btnW = 300, btnH = 50;
                if (e.getX() >= btnX && e.getX() <= btnX + btnW &&
                    e.getY() >= btnY && e.getY() <= btnY + btnH) {
                    stage.close();
                }
            }
        });

        // Erstes Zeichnen
        zeichne(gc, lama, seife, mx[0], my[0], flecken, false);

        Scene scene = new Scene(root, W, H);
        stage.setScene(scene);
        stage.show();
    }

    private static void zeichne(GraphicsContext gc, Image lama, Image seife,
                                  double mx, double my,
                                  List<boolean[]> flecken, boolean fertig) {
        // Canvas leeren
        gc.clearRect(0, 0, W, H);

        // Hintergrund – hellblau mit Fliesen
        gc.setFill(Color.rgb(200, 230, 240));
        gc.fillRect(0, 0, W, H);

        // Fliesen-Muster
        gc.setStroke(Color.rgb(180, 210, 220));
        gc.setLineWidth(1);
        for (int x = 0; x < W; x += 60) gc.strokeLine(x, 0, x, H);
        for (int y = 0; y < H; y += 60) gc.strokeLine(0, y, W, y);

        // Titel
        gc.setFill(Color.rgb(80, 50, 20));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("Lama putzen! Halte Maustaste gedrückt!", 180, 35);

        // Lama mittig
        double lamaX = W / 2.0 - 130;
        double lamaY = H / 2.0 - 200;
        gc.drawImage(lama, lamaX, lamaY, 261, 404);

        // Schmutzflecken
        if (!fertig) {
            gc.setFill(Color.rgb(101, 67, 33, 0.75));
            for (int i = 0; i < FLECKEN.length; i++) {
                if (!flecken.get(i)[0]) {
                    gc.fillOval(
                        FLECKEN[i][0] - FLECK_RADIUS,
                        FLECKEN[i][1] - FLECK_RADIUS,
                        FLECK_RADIUS * 2,
                        FLECK_RADIUS * 2
                    );
                }
            }
        }

        // Seife als Cursor
        gc.drawImage(seife, mx - 30, my - 30, 60, 60);

        // Fertig-Anzeige
        if (fertig) {
            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRoundRect(180, 200, 440, 200, 20, 20);

            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            gc.fillText("Lama ist sauber!", 240, 260);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText("+" + BELOHNUNG_XP + " XP   +" + BELOHNUNG_COINS + " Münzen", 280, 300);

            // Zurück-Button
            gc.setFill(Color.rgb(80, 160, 80));
            gc.fillRoundRect(250, 330, 300, 50, 15, 15);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.fillText("← Zurück zur Farm", 285, 362);
        }
    }
}