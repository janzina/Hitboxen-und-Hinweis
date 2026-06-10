package bta.ahaus.test.tutorial;

public enum FoodType {

    HAY("Heu"),
    CARROT("Karotte"),
    APPLE("Apfel");

    private final String displayName;

    FoodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}