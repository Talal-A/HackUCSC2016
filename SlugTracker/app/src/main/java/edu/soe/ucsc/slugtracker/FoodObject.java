package edu.soe.ucsc.slugtracker;

/**
 * Created by Wesly on 1/29/2016.
 */
public class FoodObject {
    // Constantly updated info depending on tag.
    private String tag;
    private String portion;
    private int calories;
    private int protein;
    private int fat;
    private int carbs;

    // Allergens
    private boolean egg;
    private boolean wheat;
    private boolean gluten;
    private boolean soy;
    private boolean milk;
    private boolean treeNut;
    private boolean gluten;


    public FoodObject(String tag, int calories, int protein, int fat, int carbs){
        this.tag = tag;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    // Returns name of food.
    public String getTag(){
        return tag;
    }

    // Returns portion of food.
    public String getPortion(){
        return portion;
    }

    // Return calories of food.
    public int getCal(){
        return calories;
    }

    // Return protein of food.
    public int getPro(){
        return protein;
    }

    // Return fat of food.
    public int getFat(){
        return fat;
    }

    // Return carbs of food.
    public int getCarbs(){
        return carbs;
    }
}


