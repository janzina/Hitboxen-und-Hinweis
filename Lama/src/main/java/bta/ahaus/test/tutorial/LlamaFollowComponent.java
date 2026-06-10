package bta.ahaus.test.tutorial;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Lässt das Lama dem Spieler mit einem natürlichen Nachlauf folgen.
 *
 * Funktionsprinzip:
 *   - Die vergangenen Positionen des Spielers werden in einem Ringpuffer gespeichert.
 *   - Das Lama folgt einer etwas älteren Position → wirkt wie an einer Leine.
 *   - Sobald der Abstand zu dieser Zielposition > SNAP_THRESHOLD ist, teleportiert
 *     das Lama direkt hin (verhindert Weglaufen beim Laden).
 *
 * In MVerwaltung.java:
 *   1. lama-Entity mit .with(new LlamaFollowComponent(player)) anlegen
 *   2. In onUpdate(): lama.getComponent(LlamaFollowComponent.class).onUpdate(tpf);
 *      → passiert automatisch durch FXGL
 */
public class LlamaFollowComponent extends Component {

    // ── Konstanten ────────────────────────────────────────────────────────────

    /** Abstand hinter dem Spieler (in Pixel). */
    private static final double FOLLOW_DISTANCE = 80.0;

    /** Wie schnell das Lama der Zielposition folgt (0 = sofort, 1 = nie). */
    private static final double LERP_FACTOR = 8.0;

    /** Ab diesem Abstand wird das Lama sofort teleportiert. */
    private static final double SNAP_THRESHOLD = 400.0;

    /** Wie viele Frames die Positionen gepuffert werden (= Verzögerung). */
    private static final int HISTORY_SIZE = 25;

    /** Minimale Bewegung des Spielers, bevor eine neue Position gespeichert wird. */
    private static final double MIN_MOVE = 1.5;

    // ── Felder ────────────────────────────────────────────────────────────────

    private final Entity player;
    private final Deque<Point2D> posHistory = new ArrayDeque<>();

    private Point2D lastPlayerPos = null;

    // ── Konstruktor ───────────────────────────────────────────────────────────

    public LlamaFollowComponent(Entity player) {
        this.player = player;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onAdded() {
        // Lama direkt hinter Spieler platzieren
        Point2D startPos = new Point2D(player.getX() - FOLLOW_DISTANCE, player.getY());
        entity.setPosition(startPos);
        lastPlayerPos = new Point2D(player.getX(), player.getY());

        // History mit Startpos füllen
        for (int i = 0; i < HISTORY_SIZE; i++) {
            posHistory.addLast(startPos);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D playerPos = new Point2D(player.getX(), player.getY());

        // Neue Position in History nur wenn Spieler sich bewegt hat
        if (lastPlayerPos == null ||
                playerPos.distance(lastPlayerPos) >= MIN_MOVE) {
            posHistory.addLast(playerPos);
            if (posHistory.size() > HISTORY_SIZE) {
                posHistory.removeFirst();
            }
            lastPlayerPos = playerPos;
        }

        // Zielposition = älteste Position im Puffer
        Point2D target = posHistory.peekFirst();
        if (target == null) return;

        double llamaX = entity.getX();
        double llamaY = entity.getY();
        double dist   = Math.hypot(llamaX - target.getX(), llamaY - target.getY());

        // Teleport wenn zu weit weg (z.B. Spielstart / Respawn)
        if (dist > SNAP_THRESHOLD) {
            entity.setPosition(target);
            updateAnimation(target, new Point2D(llamaX, llamaY));
            return;
        }

        // Smooth follow via Lerp
        double newX = lerp(llamaX, target.getX(), LERP_FACTOR * tpf);
        double newY = lerp(llamaY, target.getY(), LERP_FACTOR * tpf);

        // Animation nur updaten wenn Lama sich merklich bewegt
        double moved = Math.hypot(newX - llamaX, newY - llamaY);
        if (moved > 0.5) {
            updateAnimation(new Point2D(newX, newY), new Point2D(llamaX, llamaY));
            entity.getComponent(LlamaAnimationComponent.class).setMoving(
                    directionFromDelta(newX - llamaX, newY - llamaY));
        } else {
            entity.getComponent(LlamaAnimationComponent.class).setIdle();
        }

        entity.setPosition(newX, newY);
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