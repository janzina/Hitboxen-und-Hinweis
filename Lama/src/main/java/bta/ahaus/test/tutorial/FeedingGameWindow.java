package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FeedingGameWindow {

    private static final int W = 1200;
    private static final int H = 800;

    // Zonen
    private static final int FLOOR_Y    = 530;  // wo Boden beginnt
    private static final int TRAY_Y     = 620;  // untere Essens-Leiste oben
    private static final int TRAY_H     = 180;  // Höhe der Leiste

    private final Inventory inventory;
    private int     fedCount    = 0;
    private boolean rewardShown = false;

    private ProgressBar hungerBar;
    private Label       hungerLabel;

    public FeedingGameWindow(Inventory inventory) {
        this.inventory = inventory;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  SHOW
    // ═══════════════════════════════════════════════════════════════════════

    public void show() {
        Stage stage = new Stage();
        Pane  root  = new Pane();

        // ── Hintergrund / Raum ────────────────────────────────────────────
        buildRoom(root);

        // ── Lama ─────────────────────────────────────────────────────────
        ImageView llama = FXGL.texture("llama.png");
        llama.setFitWidth(300);
        llama.setFitHeight(300);
        llama.setLayoutX(440);
        llama.setLayoutY(FLOOR_Y - 290);
        DropShadow llamaShadow = new DropShadow(18, 0, 8, Color.rgb(0,0,0,0.35));
        llama.setEffect(llamaShadow);
        root.getChildren().add(llama);

        // ── Hunger-UI (oben links) ────────────────────────────────────────
        buildHungerUI(root);

        // ── Essens-Leiste (unten) ─────────────────────────────────────────
        buildTray(root);

        // ── Essen spawnen ─────────────────────────────────────────────────
        spawnFood(root, llama, PlantType.KAROTTE,   "carrot.png",   130, TRAY_Y + 40);
        spawnFood(root, llama, PlantType.KARTOFFEL, "potato.png",   430, TRAY_Y + 40);
        spawnFood(root, llama, PlantType.WEIZEN,    "wheat.png",    730, TRAY_Y + 40);

        Scene scene = new Scene(root, W, H);
        stage.setScene(scene);
        stage.setTitle("Lama Füttern");
        stage.show();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  RAUM AUFBAUEN
    // ═══════════════════════════════════════════════════════════════════════

    private void buildRoom(Pane root) {

        // Wand (Hintergrund)
        Rectangle wall = new Rectangle(0, 0, W, FLOOR_Y + 20);
        wall.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#d4c9b8")),
                new Stop(1.0, Color.web("#bfb4a2"))));
        root.getChildren().add(wall);

        // Boden
        Rectangle floor = new Rectangle(0, FLOOR_Y, W, H - FLOOR_Y);
        floor.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#8B6914")),
                new Stop(1.0, Color.web("#6b4f0e"))));
        root.getChildren().add(floor);

        // Boden-Fuge-Linien
        for (int x = 0; x < W; x += 120) {
            Line fl = new Line(x, FLOOR_Y, x, H);
            fl.setStroke(Color.web("#5a3f0a", 0.4));
            fl.setStrokeWidth(1.5);
            root.getChildren().add(fl);
        }
        for (int y = FLOOR_Y; y < H; y += 60) {
            Line fl = new Line(0, y, W, y);
            fl.setStroke(Color.web("#5a3f0a", 0.25));
            fl.setStrokeWidth(1);
            root.getChildren().add(fl);
        }

        // Wand–Boden-Leiste
        Rectangle baseboard = new Rectangle(0, FLOOR_Y - 12, W, 14);
        baseboard.setFill(Color.web("#c8b89a"));
        root.getChildren().add(baseboard);

        // Wandbilder / Dekoration
        addWallPicture(root, 60,  80, 160, 110, "🌾");
        addWallPicture(root, 950, 80, 150, 110, "🌿");

        // ── Kühlschrank (links) ───────────────────────────────────────────
        buildFridge(root, 30, FLOOR_Y - 370);

        // ── Tisch (Mitte-rechts) ──────────────────────────────────────────
        buildTable(root, 700, FLOOR_Y - 160);
    }

    /** Kleines Wandbild mit Emoji */
    private void addWallPicture(Pane root, double x, double y,
                                 double w, double h, String emoji) {
        Rectangle frame = new Rectangle(x - 5, y - 5, w + 10, h + 10);
        frame.setFill(Color.web("#8B7355"));
        frame.setArcWidth(6); frame.setArcHeight(6);

        Rectangle canvas = new Rectangle(x, y, w, h);
        canvas.setFill(Color.web("#f5f0e8"));

        Text icon = new Text(emoji);
        icon.setFont(Font.font(48));
        icon.setX(x + w / 2 - 24);
        icon.setY(y + h / 2 + 16);

        root.getChildren().addAll(frame, canvas, icon);
    }

    // ── Kühlschrank ───────────────────────────────────────────────────────

    private void buildFridge(Pane root, double x, double y) {
        double fw = 160, fh = 370;

        // Korpus
        Rectangle body = new Rectangle(x, y, fw, fh);
        body.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#e8e8e8")),
                new Stop(0.5, Color.web("#f5f5f5")),
                new Stop(1.0, Color.web("#d0d0d0"))));
        body.setArcWidth(14); body.setArcHeight(14);
        body.setEffect(new DropShadow(14, 4, 4, Color.rgb(0,0,0,0.3)));

        // Trennlinie Gefrierfach
        Rectangle divider = new Rectangle(x + 6, y + fh * 0.28, fw - 12, 5);
        divider.setFill(Color.web("#aaaaaa"));
        divider.setArcWidth(4); divider.setArcHeight(4);

        // Gefrierfach-Griff
        Rectangle handleTop = new Rectangle(x + fw / 2 - 15, y + fh * 0.28 - 40, 30, 10);
        handleTop.setFill(Color.web("#888"));
        handleTop.setArcWidth(6); handleTop.setArcHeight(6);

        // Kühlschrankgriff
        Rectangle handleBot = new Rectangle(x + fw / 2 - 15, y + fh * 0.28 + 40, 30, 10);
        handleBot.setFill(Color.web("#888"));
        handleBot.setArcWidth(6); handleBot.setArcHeight(6);

        // Kleines Licht-Highlight
        Rectangle shine = new Rectangle(x + 10, y + 10, 18, 60);
        shine.setFill(Color.rgb(255, 255, 255, 0.35));
        shine.setArcWidth(8); shine.setArcHeight(8);

        // Emoji-Magnet oben
        Text magnet = new Text("🥕");
        magnet.setFont(Font.font(26));
        magnet.setX(x + fw / 2 - 14);
        magnet.setY(y + fh * 0.15);

        // Schatten unterm Kühlschrank
        Ellipse shadow = new Ellipse(x + fw / 2, y + fh + 6, fw / 2 - 5, 8);
        shadow.setFill(Color.rgb(0, 0, 0, 0.18));

        root.getChildren().addAll(shadow, body, divider, handleTop, handleBot, shine, magnet);
    }

    // ── Tisch ─────────────────────────────────────────────────────────────

    private void buildTable(Pane root, double x, double y) {
        double tw = 380, th = 20, legH = 120, legW = 18;

        // Tischbeine
        Rectangle legL = new Rectangle(x + 20,      y + th, legW, legH);
        Rectangle legR = new Rectangle(x + tw - 38, y + th, legW, legH);
        Color legColor = Color.web("#6b3f1a");
        legL.setFill(legColor); legR.setFill(legColor);

        // Tischplatte
        Rectangle top = new Rectangle(x, y, tw, th);
        top.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#c8863c")),
                new Stop(1.0, Color.web("#a0621a"))));
        top.setArcWidth(8); top.setArcHeight(8);
        top.setEffect(new DropShadow(10, 2, 4, Color.rgb(0,0,0,0.3)));

        // Tischkante (Highlight)
        Rectangle edge = new Rectangle(x + 2, y, tw - 4, 5);
        edge.setFill(Color.web("#e0a060", 0.6));
        edge.setArcWidth(8); edge.setArcHeight(8);

        // Tisch-Schatten
        Ellipse tShadow = new Ellipse(x + tw / 2, y + th + legH + 4, tw / 2 - 10, 9);
        tShadow.setFill(Color.rgb(0, 0, 0, 0.15));

        // Dekorationen auf dem Tisch
        Text bowl = new Text("🥗");
        bowl.setFont(Font.font(38));
        bowl.setX(x + tw / 2 - 20);
        bowl.setY(y - 6);

        root.getChildren().addAll(tShadow, legL, legR, top, edge, bowl);
    }

    // ── Essens-Leiste (unten) ─────────────────────────────────────────────

    private void buildTray(Pane root) {
        // Dunkle Leiste
        Rectangle tray = new Rectangle(0, TRAY_Y, W, TRAY_H);
        tray.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#2a1f0e")),
                new Stop(1.0, Color.web("#1a1208"))));
        root.getChildren().add(tray);

        // Obere Trennlinie
        Rectangle trayLine = new Rectangle(0, TRAY_Y, W, 4);
        trayLine.setFill(Color.web("#c8863c", 0.7));
        root.getChildren().add(trayLine);

        // Label
        Label trayLabel = new Label("— Futter ziehen & ans Lama verfüttern —");
        trayLabel.setStyle(
            "-fx-text-fill: #c8a060;" +
            "-fx-font-size: 15px;" +
            "-fx-font-style: italic;");
        trayLabel.setLayoutX(W / 2.0 - 175);
        trayLabel.setLayoutY(TRAY_Y + 8);
        root.getChildren().add(trayLabel);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HUNGER-UI
    // ═══════════════════════════════════════════════════════════════════════

    private void buildHungerUI(Pane root) {
        Rectangle panel = new Rectangle(14, 14, 310, 90);
        panel.setFill(Color.rgb(0, 0, 0, 0.55));
        panel.setArcWidth(14); panel.setArcHeight(14);
        root.getChildren().add(panel);

        Label barTitle = new Label("Hunger des Lamas:");
        barTitle.setStyle("-fx-text-fill: #f0e0b0; -fx-font-size: 15px; -fx-font-weight: bold;");
        barTitle.setLayoutX(24); barTitle.setLayoutY(20);

        hungerBar = new ProgressBar(PlayerStats.getInstance().getHunger() / 100.0);
        hungerBar.setLayoutX(24); hungerBar.setLayoutY(46);
        hungerBar.setPrefWidth(290); hungerBar.setPrefHeight(20);
        updateBarColor();

        hungerLabel = new Label(hungerLabelText());
        hungerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        hungerLabel.setLayoutX(24); hungerLabel.setLayoutY(72);

        root.getChildren().addAll(barTitle, hungerBar, hungerLabel);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HUNGER HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private String hungerLabelText() {
        double h = PlayerStats.getInstance().getHunger();
        if (h >= 100) return " Das Lama ist satt! 🎉";
        if (h >= 70)  return " Hunger: " + (int) h + "% – gut versorgt";
        if (h >= 40)  return "Hunger: " + (int) h + "% – etwas hungrig";
        return               " Hunger: " + (int) h + "% – sehr hungrig!";
    }

    private void updateBarColor() {
        double h = PlayerStats.getInstance().getHunger();
        String color;
        if      (h >= 100) color = "#00e676";
        else if (h >= 70)  color = "#8bc34a";
        else if (h >= 40)  color = "#ffa726";
        else               color = "#ef5350";
        hungerBar.setStyle("-fx-accent: " + color + ";");
        hungerBar.setProgress(h / 100.0);
    }

    private void refreshHungerUI() {
        updateBarColor();
        hungerLabel.setText(hungerLabelText());
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  BELOHNUNG
    // ═══════════════════════════════════════════════════════════════════════

    private int[] calcReward() {
        if (fedCount >= 8) return new int[]{12, 35};
        if (fedCount >= 5) return new int[]{ 7, 20};
        if (fedCount >= 3) return new int[]{ 4, 10};
        return                    new int[]{ 2,  5};
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ESSEN SPAWNEN
    // ═══════════════════════════════════════════════════════════════════════

    private void spawnFood(Pane root, ImageView llama,
                           PlantType type, String texture,
                           double x, double y) {

        if (inventory.getItem(type) <= 0) return;

        ImageView food = FXGL.texture(texture);
        food.setFitWidth(90);
        food.setFitHeight(90);
        food.setLayoutX(x);
        food.setLayoutY(y);
        food.setEffect(new DropShadow(8, 2, 3, Color.rgb(0,0,0,0.4)));

        StackPane badge = ItemBadge.create(inventory.getItem(type));
        badge.setLayoutX(x + 62);
        badge.setLayoutY(y - 10);

        root.getChildren().add(badge);

        food.setOnMouseDragged(e -> {
            food.setLayoutX(e.getSceneX() - 45);
            food.setLayoutY(e.getSceneY() - 45);
            badge.setLayoutX(food.getLayoutX() + 62);
            badge.setLayoutY(food.getLayoutY() - 10);
        });

        food.setOnMouseReleased(e -> {
            if (food.getBoundsInParent().intersects(llama.getBoundsInParent())) {
                inventory.removeItem(type, 1);
                fedCount++;

                PlayerStats.getInstance().addHunger(hungerGainFor(type));
                refreshHungerUI();

                spawnHeart(root, llama.getLayoutX() + 140, llama.getLayoutY());
                root.getChildren().removeAll(food, badge);

                if (PlayerStats.getInstance().isFull() && !rewardShown) {
                    spawnFullHearts(root, llama);
                    showRewardPopup(root);
                }

                if (inventory.getItem(type) > 0) {
                    spawnFood(root, llama, type, texture, x, y);
                }
            }
        });

        root.getChildren().add(food);
    }

    private double hungerGainFor(PlantType type) {
        return switch (type) {
            case KAROTTE   -> 15;
            case KARTOFFEL -> 20;
            case WEIZEN    -> 10;
            default        -> 10;
        };
    }

    private void spawnFullHearts(Pane root, ImageView llama) {
        double[] offX = {100, 130, 170, 200, 230};
        double[] offY = {  0, -20,  10, -10,   0};
        for (int i = 0; i < offX.length; i++)
            spawnHeart(root, llama.getLayoutX() + offX[i],
                             llama.getLayoutY() + offY[i]);
    }

    private void spawnHeart(Pane root, double x, double y) {
        ImageView heart = FXGL.texture("heart.png");
        heart.setFitWidth(40);
        heart.setFitHeight(40);
        heart.setLayoutX(x);
        heart.setLayoutY(y);
        root.getChildren().add(heart);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), heart);
        tt.setByY(-150);
        tt.setOnFinished(e -> root.getChildren().remove(heart));
        tt.play();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  REWARD POPUP
    // ═══════════════════════════════════════════════════════════════════════

    private void showRewardPopup(Pane root) {
        rewardShown = true;

        int[] reward = calcReward();
        int coins = reward[0], xp = reward[1];
        inventory.addCoins(coins);
        PlayerStats.getInstance().addXP(xp);

        Pane popup = new Pane();
        popup.setLayoutX(330); popup.setLayoutY(240);

        Rectangle bg = new Rectangle(540, 220);
        bg.setArcWidth(28); bg.setArcHeight(28);
        bg.setFill(Color.rgb(10, 8, 4, 0.88));
        bg.setStroke(Color.web("#c8a040")); bg.setStrokeWidth(2);

        Label title = new Label("🦙 Lama ist satt!");
        title.setStyle("-fx-text-fill: gold; -fx-font-size: 34px; -fx-font-weight: bold;");
        title.setLayoutX(95); title.setLayoutY(30);

        Label rewardLabel = new Label("+" + xp + " XP     +" + coins + " Münzen");
        rewardLabel.setStyle("-fx-text-fill: #90ee90; -fx-font-size: 24px;");
        rewardLabel.setLayoutX(115); rewardLabel.setLayoutY(100);

        Label fedLabel = new Label("Gefüttert: " + fedCount + "x");
        fedLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px;");
        fedLabel.setLayoutX(200); fedLabel.setLayoutY(155);

        popup.getChildren().addAll(bg, title, rewardLabel, fedLabel);
        root.getChildren().add(popup);

        PauseTransition pause = new PauseTransition(Duration.seconds(4));
        pause.setOnFinished(e -> root.getChildren().remove(popup));
        pause.play();
    }
}