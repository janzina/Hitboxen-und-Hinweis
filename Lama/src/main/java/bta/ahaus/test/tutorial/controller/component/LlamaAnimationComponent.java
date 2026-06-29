package bta.ahaus.lamaDrama.controller.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LlamaAnimationComponent extends Component {

    // Spritesheet: 1756 x 981
    // 3 Spalten, 4 Reihen

    private static final int    FRAME_W        = 64;
    private static final int    FRAME_H        = 128;
    private static final int    NUM_FRAMES     = 3;
    private static final double FRAME_DURATION = 0.15;

    // Größe im Spiel
    private static final int DISPLAY_W = 90;
    private static final int DISPLAY_H = 90;

    private static final int ROW_UP = 0;
    private static final int ROW_DOWN = 1;
    private static final int ROW_RIGHT = 2;
    private static final int ROW_LEFT = 3;

    private PlayerAnimationComponent.Direction currentDirection =
            PlayerAnimationComponent.Direction.DOWN;

    private boolean moving = false;

    private int currentFrame = 1;

    private double timer = 0;

    private ImageView imageView;
    private Image spriteSheet;

    @Override
    public void onAdded() {

        spriteSheet = FXGL.getAssetLoader().loadImage("Lama.png");

        System.out.println(
                "[Llama] Sheet: "
                        + spriteSheet.getWidth()
                        + " x "
                        + spriteSheet.getHeight()
        );

        imageView = new ImageView(spriteSheet);

        imageView.setFitWidth(DISPLAY_W);
        imageView.setFitHeight(DISPLAY_H);

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        updateFrame();

        entity.getViewComponent().addChild(imageView);
    }

    @Override
    public void onUpdate(double tpf) {

        if (!moving) {

            currentFrame = 1;
            updateFrame();

            return;
        }

        timer += tpf;

        if (timer >= FRAME_DURATION) {

            timer = 0;

            currentFrame++;

            if (currentFrame >= NUM_FRAMES) {
                currentFrame = 0;
            }

            updateFrame();
        }
    }

    public void setMoving(PlayerAnimationComponent.Direction direction) {

        currentDirection = direction;
        moving = true;
    }

    public void setIdle() {

        moving = false;
    }

    private void updateFrame() {

        int row = switch (currentDirection) {

            case UP -> ROW_UP;
            case DOWN -> ROW_DOWN;
            case RIGHT -> ROW_RIGHT;
            case LEFT -> ROW_LEFT;
        };

        imageView.setViewport(
                new Rectangle2D(
                        currentFrame * FRAME_W,
                        row * FRAME_H,
                        FRAME_W,
                        FRAME_H
                )
        );
    }
}