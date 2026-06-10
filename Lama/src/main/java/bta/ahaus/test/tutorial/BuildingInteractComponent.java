package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class BuildingInteractComponent extends Component {

    private final String    name;
    private final Inventory inventory;
    private final LamaDreck lamaDreck;
    private boolean playerNearby      = false;
    private boolean isOpen            = false;
    private boolean notificationShown = false;

    // ── Vollständiger Konstruktor ─────────────────────────────────────────────
    public BuildingInteractComponent(String name, Inventory inventory, LamaDreck lamaDreck) {
        this.name      = name;
        this.inventory = inventory;
        this.lamaDreck = lamaDreck;
    }

    // ── Kompatibilitäts-Überladung (alte Aufrufe ohne Inventory/LamaDreck) ───
    public BuildingInteractComponent(String name) {
        this(name, null, null);
    }

    // ── Proximity-Logik ───────────────────────────────────────────────────────
    public void setPlayerNearby(boolean nearby) {
        if (this.playerNearby == nearby) return;
        this.playerNearby = nearby;
        if (nearby && !notificationShown) {
            FXGL.getNotificationService().pushNotification("E drücken zum " + name);
            notificationShown = true;
        }
        if (!nearby) notificationShown = false;
    }

    public boolean isPlayerNearby() { return playerNearby; }
    public boolean isOpen()         { return isOpen; }
    public String  getName()        { return name; }

    // ── Interaktions-Logik ────────────────────────────────────────────────────
    public void interact() {
        if (!playerNearby) return;

        switch (name) {

            case "Futtern":
                new FeedingGameWindow(
                        inventory != null ? inventory
                                          : MVerwaltung.getInstance().getInventory()
                ).show();
                break;

            case "Shop betreten":
                ShopMinispiel.open(
                        inventory != null ? inventory
                                          : MVerwaltung.getInstance().getInventory()
                );
                break;

            case "Putzen":
                if (lamaDreck != null && lamaDreck.kannGeputztWerden()) {
                    PutzMinispiel.open(
                            inventory != null ? inventory
                                              : MVerwaltung.getInstance().getInventory(),
                            lamaDreck
                    );
                } else {
                    FXGL.getNotificationService().pushNotification(
                            lamaDreck != null
                                    ? "Das Lama ist noch sauber! (" + lamaDreck.getDreckProzent() + "%)"
                                    : "Putzen gerade nicht möglich."
                    );
                }
                break;

            case "Erkunden":
                isOpen = !isOpen;
                System.out.println(isOpen ? "Erkunden geöffnet!" : "Erkunden geschlossen!");
                break;

            default:
                isOpen = !isOpen;
                System.out.println(isOpen ? name + " geöffnet!" : name + " geschlossen!");
                break;
        }
    }
}