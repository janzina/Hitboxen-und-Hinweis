package bta.ahaus.lamaDrama.controller.component;

import bta.ahaus.lamaDrama.controller.component.LlamaAnimationComponent;
import bta.ahaus.lamaDrama.model.data.MVerwaltung;
import bta.ahaus.lamaDrama.model.entity.Background;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import java.util.ArrayDeque;
import java.util.Deque;

public class LlamaFollowComponent extends Component {

    // ── Konstanten ────────────────────────────────────────────────────────────
    private static final double FOLLOW_DISTANCE = 80.0;
    private static final double LERP_FACTOR     = 8.0;
    private static final double SNAP_THRESHOLD  = 400.0;
    private static final int    HISTORY_SIZE    = 25;
    private static final double MIN_MOVE        = 1.5;

    // ── Felder ────────────────────────────────────────────────────────────────
    private final Entity         player;
    private final Deque<Point2D> posHistory    = new ArrayDeque<>();
    private       Point2D        lastPlayerPos = null;

    // ── Surfbrett ─────────────────────────────────────────────────────────────
    private Texture     surfboard;
    private boolean     onWater   = false;
    private Background hintergrund;

    // ── Richtung ──────────────────────────────────────────────────────────────
    private PlayerAnimationComponent.Direction lastDirection =
            PlayerAnimationComponent.Direction.DOWN;

    // ── Konstruktor ───────────────────────────────────────────────────────────
    public LlamaFollowComponent(Entity player) {
        this.player = player;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onAdded() {
        Point2D startPos = new Point2D(player.getX() - FOLLOW_DISTANCE, player.getY());
        entity.setPosition(startPos);
        lastPlayerPos = new Point2D(player.getX(), player.getY());

        for (int i = 0; i < HISTORY_SIZE; i++) {
            posHistory.addLast(startPos);
        }

        hintergrund = MVerwaltung.getInstance().getHintergrund();

        surfboard = FXGL.texture("surfboard.png");
        surfboard.setFitWidth(180);
        surfboard.setFitHeight(70);
        surfboard.setVisible(false);

        // ── Mittig unter die Füße des Lamas ──────────────────────────────
        surfboard.setTranslateX(-60);
        surfboard.setTranslateY(20);

        // ── Index 0 = hinter dem Lama-Sprite ─────────────────────────────
        
        
        entity.getViewComponent().addChild(surfboard);
        surfboard.toBack();
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D playerPos = new Point2D(player.getX(), player.getY());

        if (lastPlayerPos == null ||
                playerPos.distance(lastPlayerPos) >= MIN_MOVE) {
            posHistory.addLast(playerPos);
            if (posHistory.size() > HISTORY_SIZE) posHistory.removeFirst();
            lastPlayerPos = playerPos;
        }

        Point2D target = posHistory.peekFirst();
        if (target == null) return;

        double llamaX = entity.getX();
        double llamaY = entity.getY();
        double dist   = Math.hypot(llamaX - target.getX(), llamaY - target.getY());

        if (dist > SNAP_THRESHOLD) {
            entity.setPosition(target);
            updateAnimation(target, new Point2D(llamaX, llamaY));
            checkWater();
            return;
        }

        double newX = lerp(llamaX, target.getX(), LERP_FACTOR * tpf);
        double newY = lerp(llamaY, target.getY(), LERP_FACTOR * tpf);

        double moved = Math.hypot(newX - llamaX, newY - llamaY);
        if (moved > 0.5) {
            updateAnimation(new Point2D(newX, newY), new Point2D(llamaX, llamaY));
            lastDirection = directionFromDelta(newX - llamaX, newY - llamaY);
            entity.getComponent(LlamaAnimationComponent.class)
                  .setMoving(lastDirection);
        } else {
            entity.getComponent(LlamaAnimationComponent.class).setIdle();
        }

        entity.setPosition(newX, newY);
        checkWater();
    }

    // ── Wasser-Logik ─────────────────────────────────────────────────────────

    private void checkWater() {
        if (hintergrund == null || surfboard == null) return;

        double footX = entity.getX() + entity.getWidth()  / 2.0;
        double footY = entity.getY() + entity.getHeight();

        boolean nowOnWater = hintergrund.isWaterAt(footX, footY);

        if (nowOnWater != onWater) {
            onWater = nowOnWater;
            surfboard.setVisible(onWater);
        }

        if (onWater) {
            double rotation = switch (lastDirection) {
                case RIGHT -> 270;
                case LEFT  -> 90;
                case DOWN  -> 180;
                case UP    -> 0;
            };
            surfboard.setRotate(rotation);
        }
    }

    // ── Hilfsmethoden ────────────────────────────────────────────────────────

    private double lerp(double from, double to, double t) {
        return from + (to - from) * Math.min(t, 1.0);
    }

    private void updateAnimation(Point2D newPos, Point2D oldPos) {
        double dx = newPos.getX() - oldPos.getX();
        double dy = newPos.getY() - oldPos.getY();
        if (Math.abs(dx) < 0.1 && Math.abs(dy) < 0.1) return;
        entity.getComponent(LlamaAnimationComponent.class)
              .setMoving(directionFromDelta(dx, dy));
    }

    private PlayerAnimationComponent.Direction directionFromDelta(double dx, double dy) {
        if (Math.abs(dx) >= Math.abs(dy)) {
            return dx > 0
                    ? PlayerAnimationComponent.Direction.RIGHT
                    : PlayerAnimationComponent.Direction.LEFT;
        } else {
            return dy > 0
                    ? PlayerAnimationComponent.Direction.DOWN
                    : PlayerAnimationComponent.Direction.UP;
        }
    }
}