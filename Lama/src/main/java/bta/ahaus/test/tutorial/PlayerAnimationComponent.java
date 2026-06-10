package bta.ahaus.test.tutorial;
 
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
 
/**
 * Animiert den Spieler anhand von player_spritesheet.png.
 *
 * Sheet-Layout (64 × 128 px pro Frame, 3 Spalten × 4 Reihen):
 *   Reihe 0 → Walk Down
 *   Reihe 1 → Walk Right
 *   Reihe 2 → Walk Up
 *   Reihe 3 → Walk Left
 */
public class PlayerAnimationComponent extends Component {
 
    private static final int    FRAME_W        = 64;
    private static final int    FRAME_H        = 128;
    private static final int    NUM_FRAMES     = 3;
    private static final double FRAME_DURATION = 0.15;
 
    private static final int ROW_DOWN  = 0;
    private static final int ROW_RIGHT = 1;
    private static final int ROW_UP    = 2;
    private static final int ROW_LEFT  = 3;
 
    public enum Direction { DOWN, UP, LEFT, RIGHT }
 
    private Direction currentDirection = Direction.DOWN;
    private boolean   moving           = false;
    private int       currentFrame     = 0;
    private double    frameTimer       = 0;
 
    private ImageView imageView;
    private Image     sheetImage;
 
    @Override
    public void onAdded() {
        sheetImage = FXGL.getAssetLoader().loadImage("spieler.png");
 
        imageView = new ImageView(sheetImage);
        imageView.setFitWidth(FRAME_W);
        imageView.setFitHeight(FRAME_H);
        imageView.setPreserveRatio(false);
 
        updateViewport();
        entity.getViewComponent().addChild(imageView);
    }
 
    @Override
    public void onUpdate(double tpf) {
        if (!moving) {
            currentFrame = 0;
            frameTimer   = 0;
            updateViewport();
            return;
        }
 
        frameTimer += tpf;
        if (frameTimer >= FRAME_DURATION) {
            frameTimer -= FRAME_DURATION;
            currentFrame = (currentFrame + 1) % NUM_FRAMES;
            updateViewport();
        }
    }
 
    public void setMoving(Direction direction) {
        this.currentDirection = direction;
        this.moving           = true;
    }
 
    public void setIdle() {
        this.moving = false;
    }
 
    private void updateViewport() {
        int    row  = rowForDirection(currentDirection);
        double srcX = currentFrame * FRAME_W;
        double srcY = row          * FRAME_H;
        imageView.setViewport(new Rectangle2D(srcX, srcY, FRAME_W, FRAME_H));
    }
 
    private int rowForDirection(Direction dir) {
        return switch (dir) {
            case DOWN  -> ROW_DOWN;
            case RIGHT -> ROW_RIGHT;
            case UP    -> ROW_UP;
            case LEFT  -> ROW_LEFT;
        };
    }
}