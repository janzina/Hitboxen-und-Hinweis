package bta.ahaus.lamaDrama.view.minigame;

import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import com.almasb.fxgl.scene.SubScene;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class NuggetMachineScene extends SubScene {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private AnimationTimer gameLoop;
    private long lastTime = -1;

    private final Image lamaImage;

    private final double machX, machY, machW, machH;
    private double beltOffset = 0;
    private double shakeX = 0;
    private int shakeFrames = 0;

    private boolean lamaDropped = false;
    private boolean lamaInMachine = false;
    private double lamaY;

    private final List<Nugget> nuggets = new ArrayList<>();
    private int boxCount = 0;
    private static final int MAX_NUGGETS = 6;

    private final double boxX, boxY;

    private boolean gameOver = false;

    public NuggetMachineScene(int width, int height) {
        super();

        Image tmp;
        try {
            tmp = new Image(
                getClass().getResourceAsStream("/assets/textures/minigames/Lama_clean.png")
            );
        } catch (Exception e) {
            tmp = null;
        }
        lamaImage = tmp;

        machX = width / 2 - 150;
        machY = 50;
        machW = 300;
        machH = height - 180;
        boxX  = width / 2 + 200;
        boxY  = height - 160;
        lamaY = machY - 160;  // ← angepasst

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getContentRoot().getChildren().add(canvas);

        Button dropBtn = new Button("Lama einwerfen!");
        dropBtn.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-family: monospace;" +
            "-fx-background-color: #ffdd44;" +
            "-fx-text-fill: #333333;" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-border-color: #aa8800;" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;"
        );
        dropBtn.setLayoutX(machX + machW / 2 - 100);
        dropBtn.setLayoutY(15);
        dropBtn.setOnAction(e -> {
            if (!lamaDropped) {
                lamaDropped = true;
                dropBtn.setVisible(false);
                dropLama();
            }
        });
        getContentRoot().getChildren().add(dropBtn);

        setupClickHandler();
        startLoop();
    }

    private void setupClickHandler() {
        canvas.setOnMouseClicked(e -> {
            if (lamaInMachine) {
                nuggets.forEach(n -> {
                    if (!n.inBox && dist(e.getX(), e.getY(), n.x, n.y) < n.r + 15) {
                        double dx = boxX + 50 - n.x;
                        double dy = boxY + 40 - n.y;
                        double d  = Math.sqrt(dx * dx + dy * dy);
                        n.vx = dx / d * 14;
                        n.vy = dy / d * 14 - 6;
                        n.onGround = false;
                    }
                });
            }
        });
    }

    private void dropLama() {
        Timeline drop = new Timeline(
            new KeyFrame(Duration.seconds(1.0), e -> {
                lamaInMachine = true;
                shakeMachine();
                for (int i = 0; i < MAX_NUGGETS; i++) {
                    final int delay = i;
                    Timeline spawn = new Timeline(
                        new KeyFrame(Duration.millis(100 + 300 * delay), ev -> spawnNugget())
                    );
                    spawn.play();
                }
            })
        );
        drop.play();
    }

    private void spawnNugget() {
        Nugget n = new Nugget();
        n.x  = machX + machW / 2 + (Math.random() - 0.5) * 40;
        n.y  = machY + machH - 20;
        n.vx = (Math.random() - 0.5) * 4;
        n.vy = -5 - Math.random() * 3;
        n.r  = 14 + Math.random() * 6;
        nuggets.add(n);
    }

    private void shakeMachine() {
        shakeFrames = 30;
    }

    private void startLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime < 0) { lastTime = now; return; }
                double tpf = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                updateScene(tpf);
                render();
            }
        };
        gameLoop.start();
    }

    private void updateScene(double tpf) {
        beltOffset += 120 * tpf;
        if (beltOffset > 30) beltOffset -= 30;

        if (shakeFrames > 0) {
            shakeX = (Math.random() - 0.5) * 10;
            shakeFrames--;
        } else {
            shakeX = 0;
        }

        if (lamaDropped && !lamaInMachine) {
            lamaY += 200 * tpf;
        }

        double groundY = getAppHeight() - 80;
        nuggets.forEach(n -> {
            if (n.inBox) return;
            n.vy += 600 * tpf;
            n.x  += n.vx;
            n.y  += n.vy * tpf;

            if (n.y > groundY) {
                n.y        = groundY;
                n.vy      *= -0.4;
                n.vx      *= 0.85;
                n.onGround = true;
            }
            if (n.x < 20)                 { n.x = 20;                 n.vx *= -0.5; }
            if (n.x > getAppWidth() - 20) { n.x = getAppWidth() - 20; n.vx *= -0.5; }

            if (!n.inBox && n.x > boxX && n.x < boxX + 100 &&
                n.y >= groundY && n.onGround) {
                n.inBox = true;
                boxCount++;
                bta.ahaus.lamaDrama.model.data.MVerwaltung.getInstance()
                    .getInventory()
                    .addItem(bta.ahaus.lamaDrama.model.entity.PlantType.NUGGET, 1);
                if (boxCount >= MAX_NUGGETS) onAllCollected();
            }
        });
    }

    private void render() {
        double W = getAppWidth(), H = getAppHeight();
        gc.clearRect(0, 0, W, H);

        gc.setFill(Color.rgb(20, 20, 40, 0.95));
        gc.fillRect(0, 0, W, H);

        gc.save();
        gc.translate(shakeX, 0);
        drawMachine();
        gc.restore();

        drawNuggets();
        drawBox();
    }

    private void drawMachine() {
        LinearGradient metal = new LinearGradient(
            machX, 0, machX + machW, 0, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.rgb(60, 60, 60)),
            new Stop(0.3, Color.rgb(130, 130, 130)),
            new Stop(0.7, Color.rgb(100, 100, 100)),
            new Stop(1.0, Color.rgb(50, 50, 50))
        );
        gc.setFill(metal);
        gc.fillRect(machX, machY + 80, machW, machH - 80);

        gc.setFill(Color.rgb(90, 90, 90));
        double[] tx = { machX + 50, machX + 90, machX + machW - 90, machX + machW - 50 };
        double[] ty = { machY + 80, machY + 20, machY + 20, machY + 80 };
        gc.fillPolygon(tx, ty, 4);
        gc.setStroke(Color.rgb(40, 40, 40));
        gc.setLineWidth(2);
        gc.strokePolygon(tx, ty, 4);

        double winY = machY + 150, winH = 150;
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(machX + 20, winY, machW - 40, winH);
        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillRect(machX + 20, winY + winH - 35, machW - 40, 35);

        gc.setStroke(Color.rgb(80, 80, 80));
        gc.setLineWidth(2);
        for (double bx = machX + 20 - 30 + beltOffset; bx < machX + machW - 20; bx += 30) {
            gc.strokeLine(bx, winY + winH - 35, bx, winY + winH);
        }

        gc.setStroke(Color.rgb(150, 150, 150));
        gc.setLineWidth(3);
        gc.strokeRect(machX + 20, winY, machW - 40, winH);

        drawGear(machX + 45,         winY - 25, 20,  beltOffset * 0.1);
        drawGear(machX + machW - 45, winY - 25, 20, -beltOffset * 0.1);

        gc.setFill(Color.rgb(100, 100, 100));
        gc.fillRect(machX + machW / 2 - 35, machY + machH - 20, 70, 20);
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Monospace", 13));
        gc.fillText("NUGGIES ↓", machX + machW / 2 - 38, machY + machH + 18);

        drawGauge(machX + 50,         machY + machH - 90, "PSI");
        drawGauge(machX + machW - 50, machY + machH - 90, "RPM");

        boolean blink = (System.currentTimeMillis() / 250) % 2 == 0;
        gc.setFill(blink ? Color.RED : Color.rgb(80, 0, 0));
        gc.fillOval(machX + machW - 30, machY + 88, 20, 20);
        gc.setFill(blink ? Color.ORANGE : Color.rgb(80, 40, 0));
        gc.fillOval(machX + 10,         machY + 88, 20, 20);

        if (lamaDropped && !lamaInMachine && lamaY < machY + 90) {
            drawSimpleLama(machX + machW / 2 - 60, lamaY);  // ← zentriert
        }
    }

    private void drawSimpleLama(double x, double y) {
        if (lamaImage != null && !lamaImage.isError()) {
            gc.drawImage(lamaImage, x - 10, y, 120, 160);  // ← 3:4 proportional
        } else {
            gc.setFill(Color.rgb(212, 196, 168));
            gc.fillRoundRect(x + 15, y + 25, 70, 45, 15, 15);
            gc.fillRect(x + 35, y + 5, 22, 28);
            gc.fillOval(x + 38, y - 8, 34, 22);
            gc.setFill(Color.rgb(50, 50, 50));
            gc.fillOval(x + 55, y - 2, 7, 7);
        }
    }

    private void drawGear(double cx, double cy, double r, double angle) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(Math.toDegrees(angle));
        gc.setFill(Color.rgb(120, 120, 120));
        int teeth = 8;
        gc.beginPath();
        for (int i = 0; i < teeth; i++) {
            double a1 = i         * Math.PI * 2 / teeth;
            double a2 = (i + 0.4) * Math.PI * 2 / teeth;
            double a3 = (i + 0.6) * Math.PI * 2 / teeth;
            double a4 = (i + 1.0) * Math.PI * 2 / teeth;
            if (i == 0) gc.moveTo(Math.cos(a1) * r, Math.sin(a1) * r);
            gc.lineTo(Math.cos(a1) * r,       Math.sin(a1) * r);
            gc.lineTo(Math.cos(a2) * (r + 8), Math.sin(a2) * (r + 8));
            gc.lineTo(Math.cos(a3) * (r + 8), Math.sin(a3) * (r + 8));
            gc.lineTo(Math.cos(a4) * r,       Math.sin(a4) * r);
        }
        gc.closePath();
        gc.fill();
        gc.setFill(Color.rgb(170, 170, 170));
        gc.fillOval(-r * 0.4, -r * 0.4, r * 0.8, r * 0.8);
        gc.restore();
    }

    private void drawGauge(double cx, double cy, String label) {
        gc.setFill(Color.rgb(40, 40, 40));
        gc.fillOval(cx - 18, cy - 18, 36, 36);
        gc.setStroke(Color.rgb(120, 120, 120));
        gc.setLineWidth(2);
        gc.strokeOval(cx - 18, cy - 18, 36, 36);
        double needle = -2.0 + Math.sin(System.currentTimeMillis() * 0.002) * 0.8;
        gc.setStroke(Color.LIME);
        gc.setLineWidth(2);
        gc.strokeLine(cx, cy, cx + Math.cos(needle) * 14, cy + Math.sin(needle) * 14);
        gc.setFill(Color.GRAY);
        gc.setFont(Font.font("Monospace", 9));
        gc.fillText(label, cx - 8, cy + 28);
    }

    private void drawNuggets() {
        nuggets.forEach(n -> {
            if (n.inBox) return;
            gc.setFill(Color.rgb(212, 160, 23));
            gc.fillRoundRect(n.x - n.r, n.y - n.r * 0.7, n.r * 2, n.r * 1.4, 5, 5);
            gc.setStroke(Color.rgb(140, 90, 10));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(n.x - n.r, n.y - n.r * 0.7, n.r * 2, n.r * 1.4, 5, 5);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(12));
            gc.fillText("🍗", n.x - 8, n.y + 5);
        });
    }

    private void drawBox() {
        gc.setFill(Color.rgb(90, 58, 26));
        gc.fillRect(boxX, boxY, 100, 80);
        gc.setFill(Color.rgb(120, 90, 40));
        gc.fillRect(boxX, boxY, 100, 10);
        gc.setStroke(Color.rgb(50, 30, 10));
        gc.setLineWidth(2);
        gc.strokeRect(boxX, boxY, 100, 80);
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Monospace", 12));
        gc.fillText("NUGGIE BOX", boxX + 5, boxY - 8);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospace", 16));
        gc.fillText(boxCount + " / " + MAX_NUGGETS, boxX + 22, boxY + 48);
    }

    private void onAllCollected() {
        if (gameOver) return;
        gameOver = true;
        gameLoop.stop();

        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
        overlay.setPrefSize(getAppWidth(), getAppHeight());

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(20);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-border-color: #ffdd44;" +
            "-fx-border-width: 3;" +
            "-fx-padding: 40;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;"
        );
        box.setMaxWidth(500);
        box.setMaxHeight(350);

        javafx.scene.text.Text titel = new javafx.scene.text.Text("🎉 NUGGIE CHAMPION! 🎉");
        titel.setStyle("-fx-font-size: 26px; -fx-font-family: monospace; -fx-fill: #ffdd44;");

        javafx.scene.text.Text sub = new javafx.scene.text.Text(
            "Alle " + MAX_NUGGETS + " Nuggies eingesammelt!\nDas Lama lebt jetzt als Nugget weiter."
        );
        sub.setStyle("-fx-font-size: 15px; -fx-font-family: monospace; -fx-fill: #ffffff; -fx-text-alignment: center;");
        sub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        javafx.scene.text.Text emojiText = new javafx.scene.text.Text("🍗");
        emojiText.setStyle("-fx-font-size: 40px;");

        javafx.scene.control.Button neustarten = new javafx.scene.control.Button("🔄 Neustarten");
        neustarten.setStyle(
            "-fx-font-size: 16px; -fx-font-family: monospace;" +
            "-fx-background-color: #44aa44; -fx-text-fill: white;" +
            "-fx-padding: 10 30 10 30; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        neustarten.setOnAction(e -> {
            bta.ahaus.lamaDrama.model.data.PlayerStats.getInstance().resetXP();
            FXGL.getSceneService().popSubScene();
        });

        javafx.scene.control.Button beenden = new javafx.scene.control.Button("❌ Beenden");
        beenden.setStyle(
            "-fx-font-size: 16px; -fx-font-family: monospace;" +
            "-fx-background-color: #aa2222; -fx-text-fill: white;" +
            "-fx-padding: 10 30 10 30; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        beenden.setOnAction(e -> FXGL.getGameController().exit());

        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(20);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        buttons.getChildren().addAll(neustarten, beenden);

        box.getChildren().addAll(titel, emojiText, sub, buttons);
        overlay.getChildren().add(box);
        getContentRoot().getChildren().add(overlay);
    }

    public void onDestroy() {
        if (gameLoop != null) gameLoop.stop();
    }

    private double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    static class Nugget {
        double x, y, vx, vy, r;
        boolean inBox    = false;
        boolean onGround = false;
    }
}