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

    public BuildingInteractComponent(String name, Inventory inventory, LamaDreck lamaDreck) {
        this.name      = name;
        this.inventory = inventory;
        this.lamaDreck = lamaDreck;
    }

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
    public String getName()         { return name; }
    
    
    public void interact() {
        if (!playerNearby) return;
        if (name.equals("Putzen")) {
            if (lamaDreck.kannGeputztWerden()) {
                PutzMinispiel.open(inventory, lamaDreck);
            } else {
                FXGL.getNotificationService().pushNotification(
                    "Das Lama ist noch sauber! (" + lamaDreck.getDreckProzent() + "%)");
            }
        } else if (name.equals("Shop betreten")) {   
            ShopMinispiel.open(inventory);
        } else {
            isOpen = !isOpen;
            System.out.println(isOpen ? name + " geöffnet!" : name + " geschlossen!");
        }
    }
}
