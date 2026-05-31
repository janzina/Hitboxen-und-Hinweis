package bta.ahaus.test.tutorial;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

public class MVerwaltung extends GameApplication {

    // ── Spiel-Objekte ─────────────────────────────────────────────────────────
    private Hintergrund     hintergrund;
    private BuildingFactory uiFactory;
    private Entity          player;

    // ── Farming-System ────────────────────────────────────────────────────────
    private static final double FIELD_X = 256;   // ← anpassen!
    private static final double FIELD_Y = 256;   // ← anpassen!

    private FarmField   farmField;
    private FarmMenu    farmMenu;
    private Inventory   inventory;
    private InventoryUI inventoryUI;

    private double debugTimer = 0;

    // ── Settings ──────────────────────────────────────────────────────────────

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1800);
        settings.setHeight(1000);
        settings.setTitle("Tilemap + Buildings + Player + Farm");
        settings.setProfilingEnabled(false);
        settings.setTicksPerSecond(60);
    }

    // ── Input ─────────────────────────────────────────────────────────────────

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

        // E-Taste: Gebäude-Interaktion
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

        // Leertaste: Farm-Menü (nur wenn Spieler auf dem Feld steht)
        input.addAction(new UserAction("FARM_MENU") {
            @Override protected void onActionBegin() {
                if (farmField.contains(playerFootX(), playerFootY())) {
                    farmMenu.toggle();
                }
            }
        }, KeyCode.SPACE);
    }

    // ── Game Init ─────────────────────────────────────────────────────────────

    @Override
    protected void initGame() {
        hintergrund = new Hintergrund();
        hintergrund.loadMapWithLayers();

        uiFactory = new BuildingFactory();
        addBuildings();

        // Spieler
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

        // Näheprüfung für Gebäude-Interaktion (alle 100ms)
        // Vergleicht Spieler-Fußpunkt mit dem unteren Mittelpunkt des Gebäudes
        FXGL.getGameTimer().runAtInterval(() -> {
            double px = playerFootX();
            double py = playerFootY();
            FXGL.getGameWorld()
                .getEntitiesByType(EntityType.BUILDING)
                .forEach(s -> {
                    double bcx  = s.getX() + s.getWidth()  / 2.0;
                    double bcy  = s.getY() + s.getHeight();   // untere Kante
                    double dist = Math.hypot(px - bcx, py - bcy);
                    s.getComponent(BuildingInteractComponent.class)
                     .setPlayerNearby(dist < 300);
                });
        }, javafx.util.Duration.millis(100));

        // Farming
        inventory   = new Inventory();
        farmField   = new FarmField(FIELD_X, FIELD_Y);
        farmMenu    = new FarmMenu(inventory, farmField, () ->
                new double[]{ playerFootX(), playerFootY() });
        inventoryUI = new InventoryUI(inventory);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    protected void onUpdate(double tpf) {
        if (player != null) {
            player.getComponent(MovementComponent.class).finishFrame();
        }

        if (inventoryUI != null) {
            inventoryUI.refresh();
        }

        if (farmMenu != null && farmMenu.isVisible()) {
            if (!farmField.contains(playerFootX(), playerFootY())) {
                farmMenu.hide();
            }
        }

        // Debug: alle 3s Fußposition ausgeben (entfernen wenn Feld passt)
        debugTimer += tpf;
        if (debugTimer >= 3.0) {
            debugTimer = 0;
            System.out.printf("[DEBUG] Spieler-Fuß: (%.0f, %.0f) | Auf Feld: %b%n",
                    playerFootX(), playerFootY(),
                    farmField.contains(playerFootX(), playerFootY()));
        }
    }

    // ── Hilfsmethoden ────────────────────────────────────────────────────────

    private double playerFootX() { return player.getX() + 22 + 10; }
    private double playerFootY() { return player.getY() + 112 + 8; }

    // ── Gebäude ───────────────────────────────────────────────────────────────

    private void addBuildings() {
        uiFactory.createBuilding("Scheeune.jpg",     850,  200,  "Putzen");
        uiFactory.createBuilding("Eight_Eleven.png", 300,  1100, "Shop betreten");
        uiFactory.createBuilding("Futtier.png",      1800, 200,  "Futtern");
        uiFactory.createBuilding("Gebaude.png",      2200, 900,  "Erkunden");

        hintergrund.addBlockedArea(775,  200,  450, 450);   // Scheune
        hintergrund.addBlockedArea(250,  1100, 300, 400);   // Shop
        hintergrund.addBlockedArea(1800, 200,  550, 500);   // Füttern
        hintergrund.addBlockedArea(2190, 900,  590, 746);   // Erkunden
    }

    public static void main(String[] args) {
        launch(args);
    }
}