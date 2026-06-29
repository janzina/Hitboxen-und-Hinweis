package bta.ahaus.lamaDrama.model.data;

import bta.ahaus.lamaDrama.view.minigame.NuggetMachineScene;
import bta.ahaus.lamaDrama.controller.BuildingFactory;
import bta.ahaus.lamaDrama.controller.component.BuildingInteractComponent;
import bta.ahaus.lamaDrama.view.ui.FarmMenu;
import bta.ahaus.lamaDrama.view.ui.HudDisplay;
import bta.ahaus.lamaDrama.view.ui.InventoryUI;
import bta.ahaus.lamaDrama.view.ui.MenuSceneFactory;
import bta.ahaus.lamaDrama.controller.component.LlamaAnimationComponent;
import bta.ahaus.lamaDrama.controller.component.LlamaFollowComponent;
import bta.ahaus.lamaDrama.controller.component.MovementComponent;
import bta.ahaus.lamaDrama.controller.component.PlayerAnimationComponent;
import bta.ahaus.lamaDrama.model.entity.Background;
import bta.ahaus.lamaDrama.model.entity.LamaDreck;
import bta.ahaus.lamaDrama.model.entity.FarmField;
import bta.ahaus.lamaDrama.model.entity.EntityType;
import bta.ahaus.lamaDrama.model.entity.PlantType;
import bta.ahaus.lamaDrama.view.ui.SaveSlotMenu;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MVerwaltung extends GameApplication {

    private static MVerwaltung instance;

    public MVerwaltung() { instance = this; }

    public static MVerwaltung getInstance() { return instance; }
    public Inventory   getInventory()   { return inventory; }
    public Background  getHintergrund() { return hintergrund; }
    public LamaDreck   getLamaDreck()   { return lamaDreck; }
    public PlayerStats getPlayerStats() { return PlayerStats.getInstance(); }

    // ── Spiel-Objekte ─────────────────────────────────────────────────────────
    private Background      hintergrund;
    private BuildingFactory uiFactory;
    private Entity          player;
    private Entity          lama;
    private HudDisplay      hudDisplay;

    // ── Farming-System ────────────────────────────────────────────────────────
    private static final double FIELD_X = 256;
    private static final double FIELD_Y = 256;

    private FarmField   farmField;
    private FarmMenu    farmMenu;
    private Inventory   inventory;
    private InventoryUI inventoryUI;
    private LamaDreck   lamaDreck;

    private double debugTimer  = 0;
    private double hungerTimer = 0;

    // ── Spielstand-Verwaltung ─────────────────────────────────────────────────
    private static final String SAVE_DIR  = "saves";
    private static final String SAVE_FILE = "saves/saveslots.sav";
    private static final int    MAX_SLOTS = 3;

    private SaveSlot activeSlot = null;

    // ── Settings ──────────────────────────────────────────────────────────────
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1800);
        settings.setHeight(1000);
        settings.setTitle("LamaDrama");
        settings.setVersion("1.0");
        settings.setProfilingEnabled(false);
        settings.setTicksPerSecond(60);
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new MenuSceneFactory());
    }

    @Override
    protected void initUI() {
        // leer – Menü wird von MainMenuScene gezeichnet
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

        input.addAction(new UserAction("FARM_MENU") {
            @Override protected void onActionBegin() {
                if (farmField.contains(playerFootX(), playerFootY())) {
                    farmMenu.toggle();
                }
            }
        }, KeyCode.SPACE);

        input.addAction(new UserAction("SPEICHERN") {
            @Override protected void onActionBegin() {
                new SaveSlotMenu(SaveSlotMenu.Modus.SPEICHERN).show();
            }
        }, KeyCode.F5);

        input.addAction(new UserAction("FULLSCREEN") {
            @Override protected void onActionBegin() {
                FXGL.getPrimaryStage().setFullScreen(
                    !FXGL.getPrimaryStage().isFullScreen()
                );
            }
        }, KeyCode.F11);

        input.addAction(new UserAction("HILFE") {
            @Override protected void onActionBegin() {
                showHelpDialog();
            }
        }, KeyCode.H);
    }

    // ── initGame ──────────────────────────────────────────────────────────────
    @Override
    protected void initGame() {
        hintergrund = new Background();
        hintergrund.loadMapWithLayers();

        inventory = new Inventory();
        lamaDreck = new LamaDreck();

        if (activeSlot != null) {
            for (Map.Entry<PlantType, Integer> e : activeSlot.getInventoryItems().entrySet()) {
                inventory.addItem(e.getKey(), e.getValue());
            }
            int diff = activeSlot.getCoins() - inventory.getCoins();
            if (diff > 0)  inventory.addCoins(diff);
            else if (diff < 0) inventory.spend(-diff);

            int savedHunger = activeSlot.getHunger();
            PlayerStats.getInstance().setHunger(savedHunger > 0 ? savedHunger : 100);
            lamaDreck.setDreckLevel(activeSlot.getDreckProzent() / 100.0);
        }

        uiFactory = new BuildingFactory();
        addBuildings();

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

        player.getComponent(MovementComponent.class)
              .setHintergrund(hintergrund);

        lama = FXGL.entityBuilder()
                .at(200, 200)
                .type(EntityType.PLAYER)
                .with(new LlamaAnimationComponent())
                .with(new LlamaFollowComponent(player))
                .zIndex(99)
                .buildAndAttach();

        FXGL.getGameScene().getViewport().bindToEntity(player, 400, 300);
        FXGL.getGameScene().getViewport().setBounds(0, 0, 48 * 64, 29 * 64);

        FXGL.getGameTimer().runAtInterval(() -> {
            double px = playerFootX();
            double py = playerFootY();
            FXGL.getGameWorld()
                .getEntitiesByType(EntityType.BUILDING)
                .forEach(s -> {
                    double bcx  = s.getX() + s.getWidth()  / 2.0;
                    double bcy  = s.getY() + s.getHeight();
                    double dist = Math.hypot(px - bcx, py - bcy);
                    s.getComponent(BuildingInteractComponent.class)
                     .setPlayerNearby(dist < 300);
                });
        }, javafx.util.Duration.millis(100));

        hudDisplay  = new HudDisplay(lamaDreck);
        farmField   = new FarmField(FIELD_X, FIELD_Y);
        farmMenu    = new FarmMenu(inventory, farmField,
                () -> new double[]{ playerFootX(), playerFootY() });
        farmField.setFarmMenu(farmMenu);
        inventoryUI = new InventoryUI(inventory);

        // ── Hilfe-Button ──────────────────────────────────────────────────────
        Button helpBtn = new Button("?");
        helpBtn.setStyle(
            "-fx-background-color: #d4a017;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 40px;" +
            "-fx-min-height: 40px;"
        );
        helpBtn.setTranslateX(FXGL.getAppWidth() - 65);
        helpBtn.setTranslateY(20);
        helpBtn.setFocusTraversable(false);
        helpBtn.setOnMousePressed(e  -> e.consume());
        helpBtn.setOnMouseReleased(e -> e.consume());
        helpBtn.setOnMouseClicked(e  -> {
            e.consume();
            showHelpDialog();
            FXGL.getGameScene().getRoot().requestFocus();
        });
        FXGL.getGameScene().addUINode(helpBtn);

        if (activeSlot != null && activeSlot.getXp() > 0) {
            PlayerStats.getInstance().addXP(activeSlot.getXp());
        }

        PlayerStats.getInstance().addBlackHoleListener(() ->
            javafx.application.Platform.runLater(() ->
                FXGL.getSceneService().pushSubScene(
                    new NuggetMachineScene(
                        FXGL.getAppWidth(),
                        FXGL.getAppHeight()
                    )
                )
            )
        );
    }

    // ── Hilfe-Dialog ──────────────────────────────────────────────────────────
    private void showHelpDialog() {
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.setTitle("Spielregeln");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #2b1a0e;");


   String[] regeln = {
    "🌱  Saatgut kaufen       →  Shop",
    "🚜  Pflanzen & Ernten    →  Leertaste auf dem Feld",
    "💧  Bewässern            →  Gießkanne im Feldmenü",
    "🌾  Verkaufen            →  Shop → Kasse anklicken",
    "🛒  Spezialitäten        →  Shop (ab best. XP-Stufen)",
    "🥓  Lama füttern         →  Ernte & Spezialitäten nutzbar",
    "🚿  Lama putzen          →  alle 3 Min. bei 100%",
    "🎮  Minispiele           →  Leicht / Mittel / Schwer",
    "💰  Belohnungen          →  Münzen & XP für alles",
    "🏆  Spielziel            →  500 XP → Nuggies-Maschine",
    "💾  Speichern            →  F5",
    "🏠  Gebäude betreten     →  E",
    "❓  Hilfe                →  ? Button"
};


        for (String regel : regeln) {
            Label lbl = new Label(regel);
            lbl.setStyle(
                "-fx-text-fill: #f5deb3;" +
                "-fx-font-size: 15px;"
            );
            box.getChildren().add(lbl);
        }

        Button close = new Button("Schließen ✕");
        close.setStyle(
            "-fx-background-color: #d4a017;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8 20 8 20;"
        );
        close.setOnAction(e -> {
            dialog.close();
            FXGL.getGameScene().getRoot().requestFocus();
        });
        box.getChildren().add(close);

        javafx.scene.Scene scene = new javafx.scene.Scene(box, 450, 420);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }

    // ── onUpdate ──────────────────────────────────────────────────────────────
    @Override
    protected void onUpdate(double tpf) {
        if (player != null) {
            player.getComponent(MovementComponent.class).finishFrame();
        }

        if (inventoryUI != null) inventoryUI.refresh();
        if (hudDisplay  != null) hudDisplay.refresh();
        if (lamaDreck   != null) lamaDreck.update(tpf);

        hungerTimer += tpf;
        if (hungerTimer >= 5.0) {
            hungerTimer = 0;
            PlayerStats.getInstance().removeHunger(1);
        }

        if (farmMenu != null && farmMenu.isVisible()) {
            if (!farmField.contains(playerFootX(), playerFootY())) {
                farmMenu.hide();
            }
        }

        debugTimer += tpf;
        if (debugTimer >= 3.0) {
            debugTimer = 0;
            System.out.printf("[DEBUG] Spieler-Fuß: (%.0f, %.0f) | Auf Feld: %b%n",
                    playerFootX(), playerFootY(),
                    farmField.contains(playerFootX(), playerFootY()));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SPIELSTAND-VERWALTUNG
    // ═════════════════════════════════════════════════════════════════════════

    public List<SaveSlot> loadAllSlots(int maxSlots) {
        List<SaveSlot> result = new ArrayList<>();
        for (int i = 0; i < maxSlots; i++) result.add(null);

        File file = new File(SAVE_FILE);
        if (!file.exists()) return result;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                SaveSlot slot = SaveSlot.deserialize(line);
                if (slot != null && slot.getSlotIndex() < maxSlots)
                    result.set(slot.getSlotIndex(), slot);
            }
        } catch (IOException e) {
            System.err.println("[MVerwaltung] Fehler beim Laden: " + e.getMessage());
        }
        return result;
    }

    public void loadSlot(int index) {
        List<SaveSlot> slots = loadAllSlots(MAX_SLOTS);
        if (index >= slots.size() || slots.get(index) == null) return;
        activeSlot = slots.get(index);
        System.out.println("[MVerwaltung] Slot geladen: " + activeSlot.getCharacterName());
    }

    public void createNewSlot(int index, String characterName) {
        activeSlot = new SaveSlot(index, characterName);
        writeSlotToDisk(activeSlot);
    }

    public void saveCurrentSlot() {
        if (activeSlot == null || inventory == null) return;

        activeSlot.getInventoryItems().clear();
        for (Map.Entry<PlantType, Integer> e : inventory.getAllItems().entrySet()) {
            if (e.getValue() > 0)
                activeSlot.getInventoryItems().put(e.getKey(), e.getValue());
        }

        activeSlot.setCoins(inventory.getCoins());
        activeSlot.setXp(PlayerStats.getInstance().getXP());
        activeSlot.setHunger(PlayerStats.getInstance().getHunger());
        activeSlot.setDreckLevel(lamaDreck.getDreckProzent());

        activeSlot.updateSaveDate();
        writeSlotToDisk(activeSlot);
        System.out.println("[MVerwaltung] Gespeichert: " + activeSlot.getCharacterName());
    }

    public void applySlotToRunningGame(SaveSlot slot) {
        if (slot == null || inventory == null) return;

        inventory.getAllItems().clear();
        for (Map.Entry<PlantType, Integer> e : slot.getInventoryItems().entrySet()) {
            inventory.addItem(e.getKey(), e.getValue());
        }

        int diff = slot.getCoins() - inventory.getCoins();
        if (diff > 0)  inventory.addCoins(diff);
        else if (diff < 0) inventory.spend(-diff);

        PlayerStats.getInstance().setXp(slot.getXp());

        int savedHunger = slot.getHunger();
        PlayerStats.getInstance().setHunger(savedHunger > 0 ? savedHunger : 100);

        lamaDreck.setDreckLevel(slot.getDreckProzent() / 100.0);
    }

    public void deleteSlot(int index) {
        List<SaveSlot> slots = loadAllSlots(MAX_SLOTS);
        if (index < slots.size()) slots.set(index, null);
        writeAllSlotsToDisk(slots);
        if (activeSlot != null && activeSlot.getSlotIndex() == index)
            activeSlot = null;
    }

    public SaveSlot getActiveSlot() { return activeSlot; }

    private void writeSlotToDisk(SaveSlot slot) {
        List<SaveSlot> slots = loadAllSlots(MAX_SLOTS);
        while (slots.size() <= slot.getSlotIndex()) slots.add(null);
        slots.set(slot.getSlotIndex(), slot);
        writeAllSlotsToDisk(slots);
    }

    private void writeAllSlotsToDisk(List<SaveSlot> slots) {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
            try (BufferedWriter w = new BufferedWriter(new FileWriter(SAVE_FILE))) {
                w.write("# LamaDrama Spielstaende\n");
                for (SaveSlot s : slots) {
                    if (s != null) { w.write(s.serialize()); w.newLine(); }
                }
            }
        } catch (IOException e) {
            System.err.println("[MVerwaltung] Fehler beim Schreiben: " + e.getMessage());
        }
    }

    // ── Hilfsmethoden ─────────────────────────────────────────────────────────
    private double playerFootX() { return player.getX() + 22 + 10; }
    private double playerFootY() { return player.getY() + 112 + 8; }

    // ── Gebäude ───────────────────────────────────────────────────────────────
    private void addBuildings() {
        uiFactory.createBuilding("Scheeune.jpg",     850,  200,  "Putzen",        inventory, lamaDreck);
        uiFactory.createBuilding("Eight_Eleven.png", 300,  1100, "Shop betreten", inventory, lamaDreck);
        uiFactory.createBuilding("Futtier.png",      1800, 200,  "Futtern",       inventory, lamaDreck);
        uiFactory.createBuilding("Gebaude.png",      2200, 900,  "Erkunden",      inventory, lamaDreck);

        hintergrund.addBlockedArea(775,  200,  450, 450);
        hintergrund.addBlockedArea(200,  1100, 350, 400);
        hintergrund.addBlockedArea(1800, 200,  750, 550);
        hintergrund.addBlockedArea(2200, 1200,  590, 400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}