package edu.soe.ucsc.slugtracker;

/**
 * Created by Wesly on 1/29/2016.
 */
public class FoodObject {
    // constantly updated depending on tag
    private String tag;
    private int calories;
    private int protein;
    private int fat;
    private int carbs;

    public FoodObject(String tag, int calories, int protein, int fat, int carbs){
        this.tag = tag;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    // returns name of food
    public String getTag(){
        return tag;
    }

    // return calories of food
    public int getCal(){
        return calories;
    }

    // return protein of food
    public int getPro(){
        return protein;
    }

    // return fat of food
    public int getFat(){
        return fat;
    }

    // return carbs of food
    public int getCarbs(){
        return carbs;
    }
}


