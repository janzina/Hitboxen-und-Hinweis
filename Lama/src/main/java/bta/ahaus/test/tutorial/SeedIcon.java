package bta.ahaus.test.tutorial;

import bta.ahaus.test.tutorial.PlantType;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Erzeugt handgezeichnete Saatgut-Tütchen als JavaFX-Vektorgrafiken.
 *
 * Jede Pflanze hat:
 *   - Eine Tüte im Kraft-Paper-Look (beige/braun)
 *   - Ein charakteristisches Gemüse-Icon in der Mitte
 *   - "SEEDS"-Schriftzug oben
 *   - Lochöse oben (wie echte Saatgut-Tütchen)
 *
 * Verwendung:
 *   Group icon = SeedIcon.create(PlantType.KAROTTE, 44);
 */
public class SeedIcon {

    // Tüten-Farben (Kraft-Paper-Look)
    private static final Color BAG_LIGHT  = Color.web("#e8c88a");
    private static final Color BAG_MID    = Color.web("#d4a84b");
    private static final Color BAG_DARK   = Color.web("#b8892a");
    private static final Color BAG_SHADOW = Color.web("#8a6020");
    private static final Color TEXT_COLOR = Color.web("#5a3a10");

    /**
     * Erstellt ein Saatgut-Tüten-Icon für den angegebenen PlantType.
     *
     * @param type   Pflanzentyp
     * @param size   Zielgröße in Pixeln (Breite = size, Höhe ≈ size * 1.3)
     */
    public static Group create(PlantType type, double size) {
        Group group = new Group();
        double w = size;
        double h = size * 1.25;

        // ── Tüten-Hintergrund ──────────────────────────────────────────────
        drawBag(group, w, h);

        // ── Gemüse-Illustration ────────────────────────────────────────────
        Group veggie = switch (type) {
            case KAROTTE   -> drawCarrot(w, h);
            case KARTOFFEL -> drawPotato(w, h);
            case WEIZEN    -> drawWheat(w, h);
            case TOMATE    -> drawTomato(w, h);
            case KUERBIS   -> drawPumpkin(w, h);
            default        -> drawGeneric(type, w, h);
        };
        group.getChildren().add(veggie);

        // ── "SEEDS"-Label ──────────────────────────────────────────────────
        Text seedsText = new Text("SEEDS");
        seedsText.setFont(Font.font("Arial", FontWeight.BOLD, w * 0.18));
        seedsText.setFill(TEXT_COLOR);
        seedsText.setTextAlignment(TextAlignment.CENTER);
        seedsText.setX(w / 2 - seedsText.getLayoutBounds().getWidth() / 2);
        seedsText.setY(h * 0.22);
        group.getChildren().add(seedsText);

        // ── Lochöse ────────────────────────────────────────────────────────
        Circle holeRing = new Circle(w / 2, h * 0.06, w * 0.07);
        holeRing.setFill(BAG_DARK);
        holeRing.setStroke(BAG_SHADOW);
        holeRing.setStrokeWidth(1);
        Circle hole = new Circle(w / 2, h * 0.06, w * 0.04);
        hole.setFill(Color.web("#2b1a0e"));
        group.getChildren().addAll(holeRing, hole);

        // Leichter Schatten auf der ganzen Gruppe
        DropShadow ds = new DropShadow(4, 1, 2, Color.rgb(0, 0, 0, 0.4));
        group.setEffect(ds);

        return group;
    }

    // ── Tüten-Grundform ────────────────────────────────────────────────────

    private static void drawBag(Group g, double w, double h) {
        // Haupt-Rechteck mit Gradient
        Rectangle bag = new Rectangle(0, h * 0.04, w, h * 0.96);
        bag.setArcWidth(w * 0.12);
        bag.setArcHeight(w * 0.12);
        bag.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, BAG_LIGHT),
                new Stop(0.5, Color.web("#f0d898")),
                new Stop(1.0, BAG_MID)));
        bag.setStroke(BAG_DARK);
        bag.setStrokeWidth(1.2);
        g.getChildren().add(bag);

        // Faltlinie oben (horizontale Knicklinie wie bei echten Tütchen)
        Line fold = new Line(w * 0.08, h * 0.28, w * 0.92, h * 0.28);
        fold.setStroke(BAG_DARK);
        fold.setStrokeWidth(1.0);
        fold.setOpacity(0.5);
        g.getChildren().add(fold);

        // Faltlinie unten
        Line foldBottom = new Line(w * 0.08, h * 0.88, w * 0.92, h * 0.88);
        foldBottom.setStroke(BAG_DARK);
        foldBottom.setStrokeWidth(1.0);
        foldBottom.setOpacity(0.5);
        g.getChildren().add(foldBottom);

        // Seitliche Falzschatten (links & rechts)
        Rectangle leftShade = new Rectangle(0, h * 0.04, w * 0.06, h * 0.96);
        leftShade.setArcWidth(w * 0.12);
        leftShade.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 0, 0, 0.15)),
                new Stop(1, Color.TRANSPARENT)));
        g.getChildren().add(leftShade);
    }

    // ── Karotte ────────────────────────────────────────────────────────────

    private static Group drawCarrot(double w, double h) {
        Group g = new Group();
        double cx = w * 0.5, cy = h * 0.62;

        // Karotten-Körper (Dreieck, abgerundet)
        Polygon body = new Polygon(
                cx,        cy + h * 0.27,   // Spitze unten
                cx - w*0.18, cy - h * 0.12,
                cx + w*0.18, cy - h * 0.12);
        body.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ff6b1a")),
                new Stop(0.5, Color.web("#ff8c00")),
                new Stop(1, Color.web("#e55a00"))));
        body.setStroke(Color.web("#cc4400"));
        body.setStrokeWidth(0.8);
        body.setStrokeLineJoin(StrokeLineJoin.ROUND);
        g.getChildren().add(body);

        // Querrillen auf der Karotte
        for (int i = 1; i <= 3; i++) {
            double yy = cy - h * 0.12 + i * (h * 0.27 * 0.8 / 4.0);
            double halfW = w * 0.18 * (1.0 - i * 0.2);
            Line groove = new Line(cx - halfW * 0.7, yy, cx + halfW * 0.7, yy);
            groove.setStroke(Color.web("#cc4400", 0.4));
            groove.setStrokeWidth(0.7);
            g.getChildren().add(groove);
        }

        // Grünes Blattwerk oben
        for (int i = -1; i <= 1; i++) {
            CubicCurve leaf = new CubicCurve(
                    cx + i * w * 0.05, cy - h * 0.12,
                    cx + i * w * 0.12, cy - h * 0.28,
                    cx + i * w * 0.06, cy - h * 0.35,
                    cx + i * w * 0.03, cy - h * 0.38);
            leaf.setFill(Color.TRANSPARENT);
            leaf.setStroke(Color.web("#4a9a20"));
            leaf.setStrokeWidth(2.0);
            leaf.setStrokeLineCap(StrokeLineCap.ROUND);
            g.getChildren().add(leaf);
        }
        return g;
    }

    // ── Kartoffel ──────────────────────────────────────────────────────────

    private static Group drawPotato(double w, double h) {
        Group g = new Group();
        double cx = w * 0.5, cy = h * 0.60;

        // Unregelmäßiger Kartoffel-Körper mit Ellipse + leichte Verzerrung
        Ellipse body = new Ellipse(cx, cy, w * 0.28, h * 0.20);
        body.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#c8a05a")),
                new Stop(0.6, Color.web("#b08030")),
                new Stop(1, Color.web("#8a6020"))));
        body.setStroke(Color.web("#7a5010"));
        body.setStrokeWidth(1.0);
        g.getChildren().add(body);

        // Augen (kleine Dellen)
        for (double[] eye : new double[][]{{cx - w*0.10, cy - h*0.04}, {cx + w*0.08, cy + h*0.05}, {cx - w*0.02, cy + h*0.10}}) {
            Circle dot = new Circle(eye[0], eye[1], w * 0.025);
            dot.setFill(Color.web("#7a5010"));
            g.getChildren().add(dot);
        }

        // Kleine Unebenheit (Beule links oben)
        Ellipse bump = new Ellipse(cx - w*0.22, cy - h*0.06, w*0.07, h*0.06);
        bump.setFill(Color.web("#b88030"));
        bump.setStroke(Color.web("#7a5010"));
        bump.setStrokeWidth(0.8);
        g.getChildren().add(bump);
        return g;
    }

    // ── Weizen ─────────────────────────────────────────────────────────────

    private static Group drawWheat(double w, double h) {
        Group g = new Group();
        double cx = w * 0.5;
        double baseY = h * 0.88;
        double topY  = h * 0.32;

        Color straw  = Color.web("#c8a040");
        Color grain  = Color.web("#e8c060");
        Color stroke = Color.web("#a07820");

        // Haupthalm
        Line stem = new Line(cx, baseY, cx, topY);
        stem.setStroke(straw);
        stem.setStrokeWidth(2.0);
        stem.setStrokeLineCap(StrokeLineCap.ROUND);
        g.getChildren().add(stem);

        // Seitenhalme
        for (int side : new int[]{-1, 1}) {
            Line branch = new Line(cx, baseY - h * 0.08, cx + side * w * 0.16, baseY - h * 0.22);
            branch.setStroke(straw);
            branch.setStrokeWidth(1.5);
            branch.setStrokeLineCap(StrokeLineCap.ROUND);
            g.getChildren().add(branch);
        }

        // Ähren-Körner (elliptische Körner entlang des Halms)
        double[] grainYs = {topY, topY + h*0.05, topY + h*0.10, topY + h*0.15, topY + h*0.20};
        for (int i = 0; i < grainYs.length; i++) {
            for (int side : new int[]{-1, 1}) {
                Ellipse kernel = new Ellipse(cx + side * w * 0.08, grainYs[i], w * 0.075, h * 0.045);
                kernel.setFill(grain);
                kernel.setStroke(stroke);
                kernel.setStrokeWidth(0.7);
                // Grannen (dünne Linien oben)
                Line awn = new Line(cx + side * w*0.08, grainYs[i] - h*0.045,
                                    cx + side * w*0.11, grainYs[i] - h*0.11);
                awn.setStroke(stroke);
                awn.setStrokeWidth(0.6);
                g.getChildren().addAll(kernel, awn);
            }
        }
        return g;
    }

    // ── Tomate ─────────────────────────────────────────────────────────────

    private static Group drawTomato(double w, double h) {
        Group g = new Group();
        double cx = w * 0.5, cy = h * 0.60;
        double r  = w * 0.24;

        // Tomate (Kreis mit Gradient)
        Circle body = new Circle(cx, cy, r);
        body.setFill(new LinearGradient(0.2, 0, 0.8, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ff5555")),
                new Stop(0.5, Color.web("#e82020")),
                new Stop(1, Color.web("#c01010"))));
        body.setStroke(Color.web("#a00000"));
        body.setStrokeWidth(1.0);
        g.getChildren().add(body);

        // Glanzfleck
        Ellipse shine = new Ellipse(cx - r*0.3, cy - r*0.3, r*0.18, r*0.12);
        shine.setFill(Color.rgb(255, 255, 255, 0.35));
        g.getChildren().add(shine);

        // Kelchblätter (Sternform oben)
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72 - 90);
            CubicCurve leaf = new CubicCurve(
                    cx, cy - r * 0.95,
                    cx + Math.cos(angle - 0.4) * r * 0.5, cy - r * 1.35,
                    cx + Math.cos(angle + 0.4) * r * 0.5, cy - r * 1.35,
                    cx + Math.cos(angle) * r * 0.55, cy - r * 0.85);
            leaf.setFill(Color.web("#2d8a20"));
            leaf.setStroke(Color.web("#1a5a10"));
            leaf.setStrokeWidth(0.5);
            g.getChildren().add(leaf);
        }

        // Stiel
        Line stiel = new Line(cx, cy - r, cx + w*0.03, cy - r - h*0.10);
        stiel.setStroke(Color.web("#4a7a20"));
        stiel.setStrokeWidth(2.0);
        stiel.setStrokeLineCap(StrokeLineCap.ROUND);
        g.getChildren().add(stiel);
        return g;
    }

    // ── Kürbis ─────────────────────────────────────────────────────────────

    private static Group drawPumpkin(double w, double h) {
        Group g = new Group();
        double cx = w * 0.5, cy = h * 0.62;

        Color orange     = Color.web("#e87010");
        Color darkOrange = Color.web("#c05000");
        Color lightOrange= Color.web("#f09030");

        // 3 Kürbis-Segmente (überlappende Ellipsen)
        double[][] segs = {{cx - w*0.12, cy, w*0.16, h*0.22}, {cx, cy - h*0.02, w*0.20, h*0.24}, {cx + w*0.12, cy, w*0.16, h*0.22}};
        Color[] cols    = {Color.web("#d06020"), orange, Color.web("#d06020")};
        for (int i = 0; i < segs.length; i++) {
            Ellipse seg = new Ellipse(segs[i][0], segs[i][1], segs[i][2], segs[i][3]);
            seg.setFill(cols[i]);
            seg.setStroke(darkOrange);
            seg.setStrokeWidth(0.8);
            g.getChildren().add(seg);
        }

        // Rillen zwischen Segmenten
        for (double rx : new double[]{cx - w*0.04, cx + w*0.04}) {
            Line rib = new Line(rx, cy - h*0.22, rx, cy + h*0.22);
            rib.setStroke(darkOrange);
            rib.setStrokeWidth(1.2);
            rib.setOpacity(0.6);
            g.getChildren().add(rib);
        }

        // Glanzfleck
        Ellipse shine = new Ellipse(cx - w*0.06, cy - h*0.12, w*0.06, h*0.04);
        shine.setFill(Color.rgb(255, 220, 150, 0.40));
        g.getChildren().add(shine);

        // Stiel
        CubicCurve stem = new CubicCurve(cx, cy - h*0.24, cx - w*0.05, cy - h*0.34, cx + w*0.08, cy - h*0.38, cx + w*0.06, cy - h*0.42);
        stem.setFill(Color.TRANSPARENT);
        stem.setStroke(Color.web("#5a7a20"));
        stem.setStrokeWidth(2.5);
        stem.setStrokeLineCap(StrokeLineCap.ROUND);
        g.getChildren().add(stem);

        // Ranke
        CubicCurve tendril = new CubicCurve(cx + w*0.06, cy - h*0.38, cx + w*0.22, cy - h*0.44, cx + w*0.26, cy - h*0.34, cx + w*0.20, cy - h*0.28);
        tendril.setFill(Color.TRANSPARENT);
        tendril.setStroke(Color.web("#5a7a20"));
        tendril.setStrokeWidth(1.0);
        g.getChildren().add(tendril);
        return g;
    }

    // ── Generischer Fallback (Emoji auf Tüte) ──────────────────────────────

    private static Group drawGeneric(PlantType type, double w, double h) {
        Group g = new Group();
        Text emoji = new Text(type.emoji);
        emoji.setFont(Font.font(w * 0.50));
        double ew = emoji.getLayoutBounds().getWidth();
        double eh = emoji.getLayoutBounds().getHeight();
        emoji.setX(w / 2 - ew / 2);
        emoji.setY(h * 0.75 - eh / 4);
        g.getChildren().add(emoji);
        return g;
    }
}