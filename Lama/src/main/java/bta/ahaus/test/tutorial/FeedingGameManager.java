package bta.ahaus.test.tutorial;

import java.util.Random;

public class FeedingGameManager {

    private final Random random = new Random();

    private FoodType desiredFood;
    private int score;
    private int feedCount;

    public FeedingGameManager() {
        generateNewWish();
    }

    public void generateNewWish() {

        FoodType[] foods = FoodType.values();

        desiredFood =
                foods[random.nextInt(foods.length)];
    }

    public boolean feed(FoodType food) {

        boolean correct = food == desiredFood;

        if (correct) {
            score += 20;
            feedCount++;
        } else {
            score += 5;
        }

        if (!isFinished()) {
            generateNewWish();
        }

        return correct;
    }

    public FoodType getDesiredFood() {
        return desiredFood;
    }

    public int getScore() {
        return score;
    }

    public boolean isFinished() {
        return feedCount >= 5;
    }
}