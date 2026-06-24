package bta.ahaus.test.tutorial;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class KistenKarte extends StackPane {

    private final int gewicht;

    public KistenKarte(String name, int gewicht) {

        this.gewicht = gewicht;

        Rectangle bg = new Rectangle(120, 80);
        bg.setArcWidth(15);
        bg.setArcHeight(15);
        bg.setFill(Color.SANDYBROWN);

        Label text = new Label(name);

        text.setStyle(
                "-fx-font-size:18;" +
                "-fx-font-weight:bold;"
        );

        getChildren().addAll(bg, text);
    }

    public int getGewicht() {
        return gewicht;
    }
}