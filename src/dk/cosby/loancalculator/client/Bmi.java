package dk.cosby.loancalculator.client;

import java.io.Serializable;

public class Bmi implements Serializable {

    private int height;
    private int weight;

    public Bmi(int height, int weight) {
        this.height = height;
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
