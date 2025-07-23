package model;

//the Ingredient class represents an individual ingredient used in a food item
public class Ingredient {
	
	//declare variables
    private int foodId;
    private String foodName;
    private double quantityInGrams;

    
    /**
     * constructor for Ingredient with the specified attributes
     *
     * @param foodId the ID of the food item
     * @param foodName the name of the food item
     * @param quantityInGrams the quantity of the ingredient in grams
     */
    public Ingredient(int foodId, String foodName, double quantityInGrams) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.quantityInGrams = quantityInGrams;
    }

    //getters for variables
    
    /**
     * @return the food id
     */
    public int getFoodId() {
        return foodId;
    }

    /**
     * @return the food name
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * @return the quantity in grams
     */
    public double getQuantityInGrams() {
        return quantityInGrams;
    }

    //toString method
    @Override
    public String toString() {
        return foodName + " (" + quantityInGrams + " g)";
    }
}
