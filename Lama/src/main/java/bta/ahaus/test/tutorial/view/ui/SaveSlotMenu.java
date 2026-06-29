package bta.ahaus.lamaDrama.view.ui;

import bta.ahaus.lamaDrama.model.data.MVerwaltung;
import bta.ahaus.lamaDrama.model.data.SaveSlot;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;

public class SaveSlotMenu {

    public enum Modus {
        SPEICHERN,
        LADEN
    }

    private final Modus modus;
    private final Stage stage;

    public SaveSlotMenu(Modus modus) {
        this.modus = modus;
        this.stage = new Stage();
    }

    public void show() {
        String fenstTitel = modus == Modus.SPEICHERN
            ? "Spielstand speichern"
            : "Spielstand laden";
        stage.setTitle(fenstTitel);

        Text ueberschrift = new Text(fenstTitel);
        ueberschrift.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        ueberschrift.setFill(Color.web("#FF6F00"));

        List<SaveSlot> slots = MVerwaltung.getInstance().loadAllSlots(3);

        HBox slotBox = new HBox(20);
        slotBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            slotBox.getChildren().add(erstelleSlotKarte(i, slots.get(i)));
        }

        Button btnAbbrechen = new Button("Abbrechen");
        btnAbbrechen.setOnAction(e -> stage.close());
        btnAbbrechen.setStyle(
            "-fx-background-color: #EF5350;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-font-size: 16px;"
        );

        VBox layout = new VBox(30, ueberschrift, slotBox, btnAbbrechen);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FFF9C4; -fx-padding: 40;");

        stage.setScene(new Scene(layout, 680, 340));
        stage.show();
    }

    private VBox erstelleSlotKarte(int index, SaveSlot slot) {

        Text slotTitel = new Text("Slot " + (index + 1));
        slotTitel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Text info;
        if (slot == null) {
            info = new Text("[ Leer ]");
            info.setFill(Color.GRAY);
        } else {
            info = new Text(
                "👤 " + slot.getCharacterName() + "\n" +
                "📅 " + slot.getFormattedDate() + "\n" +
                "⭐ XP: "     + slot.getXp()     + "\n" +
                "💰 Münzen: " + slot.getCoins()  + "\n" +
                "🍽 Hunger: "  + slot.getHunger() + "%\n" +
                "🚿 Dreck: "  + slot.getDreckProzent()   + "%"
            );
            info.setFill(Color.web("#336900"));
        }
        info.setFont(Font.font("Arial", 13));

        Button btn = new Button(modus == Modus.SPEICHERN ? "💾 Speichern" : "📂 Laden");
        btn.setPrefWidth(160);
        btn.setStyle(
            "-fx-background-color: #FF6F00;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-font-size: 15px;"
        );

        btn.setOnAction(e -> {
            if (modus == Modus.SPEICHERN) {
                MVerwaltung mv = MVerwaltung.getInstance();
                if (mv.getActiveSlot() == null) {
                    mv.createNewSlot(index, "Spieler " + (index + 1));
                }
                mv.saveCurrentSlot();
                FXGL.getNotificationService()
                    .pushNotification("Slot " + (index + 1) + " gespeichert ✓");
            } else {
                if (slot == null) {
                    FXGL.getNotificationService()
                        .pushNotification("Slot " + (index + 1) + " ist leer!");
                    return;
                }
                MVerwaltung mv = MVerwaltung.getInstance();
                mv.loadSlot(index);
                mv.applySlotToRunningGame(mv.getActiveSlot()); // ← Werte sofort anwenden
                FXGL.getNotificationService()
                    .pushNotification("Slot " + (index + 1) + " geladen ✓");
            }
            stage.close();
        });

        VBox karte = new VBox(10, slotTitel, info, btn);
        karte.setAlignment(Pos.CENTER);
        karte.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3);"
        );
        return karte;
    }
}