package bta.ahaus.test.tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class BuildingInteractComponent extends Component {

    private boolean playerNearby = false;
    private boolean isOpen = false;
    private String name;
    private boolean notificationShown = false;

    public BuildingInteractComponent(String name) {
        this.name = name;
    }

    public void setPlayerNearby(boolean nearby) {
        if (this.playerNearby == nearby) return;
        this.playerNearby = nearby;
        if (nearby && !notificationShown) {
            FXGL.getNotificationService().pushNotification("E drücken zum " + name);
            notificationShown = true;
        }
        if (!nearby) {
            notificationShown = false;
        }
    }

    public boolean isPlayerNearby() { return playerNearby; }
    public boolean isOpen()         { return isOpen; }
    public String getName()         { return name; }

    public void interact() {
        if (!playerNearby) return;
        isOpen = !isOpen;
        System.out.println(isOpen ? name + " geöffnet!" : name + " geschlossen!");
    }
}