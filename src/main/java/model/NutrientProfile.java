package model;

//imports
import java.util.HashMap;
import java.util.Map;


/**
 * represents the nutritional information of a specific food item.
 * stores the name of the food and a map of nutrient names to their respective amounts.
 */
public class NutrientProfile {
	
	//declare variables
    private String foodName;
    private Map<String, Double> nutrientAmounts = new HashMap<>();	//holds the nutrient amount information

    
    //getters and setters for variables
    
    /**
     * @return the food name
     */
    public String getFoodName() {
        return foodName;
    }

    
    /**
     * @param foodName the food name to set
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    
    /**
     * each key is the name of a nutrient, and each value is the amount of that nutrient.
     * @return the map of nutrient names to their respective amounts
     */
    public Map<String, Double> getNutrientAmounts() {
        return nutrientAmounts;
    }

    
    /**
     * sets the map of nutrient amounts.
     *
     * @param nutrientAmounts a map containing nutrient names and their corresponding amounts
     */
    public void setNutrientAmounts(Map<String, Double> nutrientAmounts) {
        this.nutrientAmounts = nutrientAmounts;
    }
}
