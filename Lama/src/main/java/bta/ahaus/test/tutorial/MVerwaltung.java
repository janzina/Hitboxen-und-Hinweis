package bta.ahaus.test.tutorial;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

public class MVerwaltung extends GameApplication {

    private Hintergrund     hintergrund;
    private BuildingFactory uiFactory;
    private Entity          player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1800);
        settings.setHeight(1000);
        settings.setTitle("Tilemap + Buildings + Player");
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("LEFT") {
            @Override protected void onAction() {
                player.getComponent(MovementComponent.class).left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("RIGHT") {
            @Override protected void onAction() {
                player.getComponent(MovementComponent.class).right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("UP") {
            @Override protected void onAction() {
                player.getComponent(MovementComponent.class).up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("DOWN") {
            @Override protected void onAction() {
                player.getComponent(MovementComponent.class).down();
            }
        }, KeyCode.S);

        // NEU – E-Taste für Interaktion
        input.addAction(new UserAction("INTERACT") {
            @Override protected void onActionBegin() {
                FXGL.getGameWorld()
                    .getEntitiesByType(EntityType.BUILDING)
                    .forEach(s -> {
                        BuildingInteractComponent bc =
                            s.getComponent(BuildingInteractComponent.class);
                        if (bc.isPlayerNearby()) bc.interact();
                    });
            }
        }, KeyCode.E);
    }
    @Override
    protected void initGame() {
        hintergrund = new Hintergrund();
        hintergrund.loadMapWithLayers();
        uiFactory = new BuildingFactory();
        addBuildings();

        // NEU – Gebäude blockieren
        hintergrund.addBlockedArea(525,  150,  420,   450);   // Putzen
        hintergrund.addBlockedArea(220,  1100, 300,  400);   // Shop
        hintergrund.addBlockedArea(1800, 200,  550,  500);   // Füttern
        hintergrund.addBlockedArea(2190, 900,  590,   746);   // Erkunden

        player = FXGL.entityBuilder()
                .at(200, 200)
                .type(EntityType.PLAYER)
                .with(new MovementComponent())
                .with(new PlayerAnimationComponent())
                .zIndex(100)
                .buildAndAttach();
        player.getBoundingBoxComponent()
              .addHitBox(new HitBox(
                      new Point2D(22, 112),
                      BoundingShape.box(20, 16)));
        player.getComponent(MovementComponent.class).setHintergrund(hintergrund);
        FXGL.getGameScene()
            .getViewport()
            .bindToEntity(player, 400, 300);
        FXGL.getGameTimer().runAtInterval(() -> {
            FXGL.getGameWorld()
                .getEntitiesByType(EntityType.BUILDING)
                .forEach(s -> {
                    double dist = player.getPosition().distance(s.getPosition());
                    s.getComponent(BuildingInteractComponent.class)
                     .setPlayerNearby(dist < 300);
                });
        }, javafx.util.Duration.millis(100));
}
  
    @Override
    protected void onUpdate(double tpf) {
        if (player != null) {
            player.getComponent(MovementComponent.class).finishFrame();
        }
    }

    // NEU – createBuildingEntity statt createBuilding
    private void addBuildings() {
        uiFactory.createBuildingEntity("Scheeune.jpg",    600,  150, "Putzen");
        uiFactory.createBuildingEntity("Eight_Eleven.png", 300, 1100, "Shop betreten");
        uiFactory.createBuildingEntity("Futtier.png",    1800,  200, "Füttern");
        uiFactory.createBuildingEntity("Gebaude.png",    2200,  900, "Erkunden");
        
        hintergrund.addBlockedArea(525,  150,  420,   450);   // Putzen
        hintergrund.addBlockedArea(250,  1100, 300,  400);   // Shop
        hintergrund.addBlockedArea(1800, 200,  550,  500);   // Füttern
        hintergrund.addBlockedArea(2190, 900,  590,   746);   // Erkunden
        
    }


    public static void main(String[] args) {
        launch(args);
    }
}