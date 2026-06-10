/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package bta.ahaus.test.tutorial;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class ItemBadge {

    public static StackPane create(int amount) {

        Circle circle = new Circle(14);
        circle.setStyle("-fx-fill: white; -fx-stroke: black;");

        Label label = new Label(String.valueOf(amount));
        label.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;");

        return new StackPane(circle, label);
    }

    public static void update(StackPane badge, int amount) {

        Label label = (Label) badge.getChildren().get(1);
        label.setText(String.valueOf(amount));
    }
}