package edu.soe.ucsc.slugtracker;

/**
 * Created by Wesly on 1/29/2016.
 */
public class FoodObject {
    // Constantly updated info depending on tag.
    private String tag;
    private String portion;
    private int calories;
    private float protein;
    private float fat;
    private float carbs;

    // Allergens
    private boolean egg;
    private boolean wheat;
    private boolean soy;
    private boolean milk;
    private boolean treeOrPNut;
    private boolean gluten;
    private boolean fishShell;

    // Instantiate food object, and sets basic info.
    public FoodObject(String tag, int calories, float protein, float fat, float carbs){
        this.tag = tag;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.tag = tag;

    }

    // Sets allergen info.
    public void addAllergens(boolean milk, boolean soy, boolean treeOrPNut, boolean gluten, boolean fishShell, boolean egg, boolean wheat){
        this.egg = egg;
        this.wheat = wheat;
        this.fishShell = fishShell;
        this.gluten = gluten;
        this.treeOrPNut = treeOrPNut;
        this.soy = soy;
        this.milk = milk;
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
    public float getPro(){
        return protein;
    }

    // Return fat of food.
    public float getFat(){
        return fat;
    }

    // Return carbs of food.
    public float getCarbs(){
        return carbs;
    }

    public String getAllergens() {

        String toReturn = "";

        if (egg)
            toReturn += "Eggs, ";
        if (wheat)
            toReturn += "Wheat, ";
        if (fishShell)
            toReturn += "Fish/Shellfish, ";
        if (gluten)
            toReturn += "Gluten, ";
        if (treeOrPNut)
            toReturn += "Nuts, ";
        if (soy)
            toReturn += "Soy, ";
        if (milk)
            toReturn += "Dairy, ";

        return toReturn.substring(0, toReturn.length() - 2);

        
    }

}


