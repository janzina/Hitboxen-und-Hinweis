package bta.ahaus.lamaDrama.controller.component;

import bta.ahaus.lamaDrama.model.entity.Background;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TransformComponent;

public class MovementComponent extends Component {

    private TransformComponent xyHandler;
    private Background        hintergrund;
    private double             speed;

    // Sprite ist 64x128px → Füße sind unten mittig
    // Wir definieren eine schmale Fußbox in der unteren Hälfte des Sprites
    private static final double SPRITE_W   = 64;
    private static final double SPRITE_H   = 128;

    // Fußbox: schmales Rechteck unten in der Mitte des Sprites
    private static final double FOOT_W     = 20;  // Breite der Fußbox
    private static final double FOOT_H     = 16;  // Höhe der Fußbox
    private static final double FOOT_OFF_X = (SPRITE_W - FOOT_W) / 2.0;  // = 22
    private static final double FOOT_OFF_Y = SPRITE_H - FOOT_H;           // = 112

    private boolean moved = false;

    public void setHintergrund(Background hintergrund) {
        this.hintergrund = hintergrund;
    }

    @Override
    public void onAdded() {
        xyHandler = entity.getTransformComponent();
    }

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * 200;
        moved = false;
    }

    public void up()    { if (tryMove(0,      -speed)) animate(PlayerAnimationComponent.Direction.UP);    }
    public void down()  { if (tryMove(0,       speed)) animate(PlayerAnimationComponent.Direction.DOWN);  }
    public void left()  { if (tryMove(-speed,  0))     animate(PlayerAnimationComponent.Direction.LEFT);  }
    public void right() { if (tryMove( speed,  0))     animate(PlayerAnimationComponent.Direction.RIGHT); }

    /**
     * Prüft Kollision nur anhand der Fußbox (unterer Bereich des Sprites).
     * X und Y getrennt → Spieler gleitet an Wänden entlang.
     */
    private boolean tryMove(double dx, double dy) {
        if (hintergrund == null) return false;

        boolean movedAny = false;
        double  cx = entity.getX();
        double  cy = entity.getY();

        // Fußbox-Position bei aktueller Entity-Position
        double fx = cx + FOOT_OFF_X;
        double fy = cy + FOOT_OFF_Y;

        // X-Achse
        if (dx != 0 && isFootAreaWalkable(fx + dx, fy)) {
            xyHandler.translateX(dx);
            movedAny = true;
        }

        // Y-Achse (mit ggf. aktualisiertem X)
        double fxNew = entity.getX() + FOOT_OFF_X;
        if (dy != 0 && isFootAreaWalkable(fxNew, fy + dy)) {
            xyHandler.translateY(dy);
            movedAny = true;
        }

        if (movedAny) moved = true;
        return movedAny;
    }

    /**
     * Prüft alle 4 Ecken der Fußbox.
     */
    private boolean isFootAreaWalkable(double fx, double fy) {
        return hintergrund.isWalkableAt(fx,            fy)
            && hintergrund.isWalkableAt(fx + FOOT_W,   fy)
            && hintergrund.isWalkableAt(fx,            fy + FOOT_H)
            && hintergrund.isWalkableAt(fx + FOOT_W,   fy + FOOT_H);
    }

    private void animate(PlayerAnimationComponent.Direction dir) {
        if (entity.hasComponent(PlayerAnimationComponent.class)) {
            entity.getComponent(PlayerAnimationComponent.class).setMoving(dir);
        }
    }

    public void finishFrame() {
        if (!moved && entity.hasComponent(PlayerAnimationComponent.class)) {
            entity.getComponent(PlayerAnimationComponent.class).setIdle();
        }
        moved = false;
    }
}