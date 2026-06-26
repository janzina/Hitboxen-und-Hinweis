
package bta.ahaus.lamaDrama.controller.component;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Gießkannen-Animation: schwingendes Icon + Wasserpartikel.
 *
 * Die Animation läuft direkt im GameScene-Koordinatensystem (Weltkoordinaten).
 * Sie wird über WateringAnimation.play(worldX, worldY) gestartet.
 */
public class WateringAnimation {

    private static final String CAN_PNG = "assets/textures/tools/watering_can.png";
    private static final int    PARTICLE_COUNT = 18;

    // Wird einmalig erzeugt und wiederverwendet
    private final ImageView canView;
    private final Pane      particlePane;

    public WateringAnimation() {
        // ── Gießkannen-Icon ───────────────────────────────────────────────────
        canView = new ImageView();
        canView.setFitWidth(52);
        canView.setFitHeight(52);
        canView.setPreserveRatio(true);
        canView.setSmooth(true);
        canView.setVisible(false);

        // Rotationspunkt: unten-links der Kanne (Ausguss)
        canView.setTranslateX(-26);
        canView.setTranslateY(-52);

        try {
            Image img = new Image(
                    getClass().getClassLoader().getResourceAsStream(CAN_PNG),
                    52, 52, true, true);
            if (!img.isError()) canView.setImage(img);
        } catch (Exception ignored) {
            // PNG fehlt → Animation läuft ohne Icon (nur Partikel)
        }

        // ── Partikel-Container ────────────────────────────────────────────────
        particlePane = new Pane();
        particlePane.setMouseTransparent(true);
        particlePane.setVisible(false);

        FXGL.getGameScene().addUINode(particlePane);
        FXGL.getGameScene().addUINode(canView);
    }

    /**
     * Startet die Animation über der angegebenen Welt-Position.
     *
     * @param worldX  Welt-X (Zellmitte)
     * @param worldY  Welt-Y (Zellmitte)
     */
    public void play(double worldX, double worldY) {
        // Welt → Bildschirm-Koordinaten
        double screenX = worldX - FXGL.getGameScene().getViewport().getX();
        double screenY = worldY - FXGL.getGameScene().getViewport().getY();

        // Gießkanne etwas oberhalb der Zelle positionieren
        canView.setLayoutX(screenX - 10);
        canView.setLayoutY(screenY - 60);
        canView.setVisible(true);
        canView.setRotate(0);

        // ── Schwinge: Drehung 0° → -40° → 0° ────────────────────────────────
        RotateTransition swing = new RotateTransition(Duration.millis(600), canView);
        swing.setFromAngle(0);
        swing.setByAngle(-45);
        swing.setCycleCount(2);
        swing.setAutoReverse(true);
        swing.setInterpolator(Interpolator.EASE_BOTH);

        // ── Partikel ──────────────────────────────────────────────────────────
        particlePane.getChildren().clear();
        particlePane.setVisible(true);

        List<Timeline> particleTimelines = new ArrayList<>();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double delay  = 150 + i * 25;             // Versatz: erst wenn Kanne kippt
            double angle  = Math.toRadians(200 + Math.random() * 80); // Strahl nach unten-rechts
            double speed  = 40 + Math.random() * 60;
            double size   = 3 + Math.random() * 4;
            double alpha  = 0.7 + Math.random() * 0.3;

            Circle drop = new Circle(size, Color.DEEPSKYBLUE);
            drop.setOpacity(alpha);
            drop.setLayoutX(screenX + 8);
            drop.setLayoutY(screenY - 30);
            particlePane.getChildren().add(drop);

            double targetX = drop.getLayoutX() + Math.cos(angle) * speed;
            double targetY = drop.getLayoutY() + Math.sin(angle) * speed;

            Timeline tl = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(drop.layoutXProperty(), drop.getLayoutX()),
                            new KeyValue(drop.layoutYProperty(), drop.getLayoutY()),
                            new KeyValue(drop.opacityProperty(), alpha)),
                    new KeyFrame(Duration.millis(500),
                            new KeyValue(drop.layoutXProperty(), targetX, Interpolator.EASE_OUT),
                            new KeyValue(drop.layoutYProperty(), targetY, Interpolator.EASE_IN),
                            new KeyValue(drop.opacityProperty(), 0.0)));
            tl.setDelay(Duration.millis(delay));
            particleTimelines.add(tl);
        }

        // Alles gleichzeitig starten
        ParallelTransition all = new ParallelTransition(swing);
        all.getChildren().addAll(particleTimelines);
        all.setOnFinished(e -> {
            canView.setVisible(false);
            particlePane.setVisible(false);
            particlePane.getChildren().clear();
        });
        all.play();
    }
}