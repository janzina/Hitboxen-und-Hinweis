package bta.ahaus.test.tutorial;


import javafx.scene.image.ImageView;

public class FoodItem extends ImageView {

    private final FoodType type;

    public FoodItem(String texture, FoodType type) {

        super(texture);

        this.type = type;

        setFitWidth(100);
        setFitHeight(100);
    }

    public FoodType getType() {
        return type;
    }
}