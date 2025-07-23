package model;


//imports
import java.util.ArrayList;

import java.util.Date;
import java.util.List;


/**
 * the Meal class represents a meal eaten by the user (breakfast, lunch, dinner, snack)
 *
 */
public class Meal {
	
	//declare variables
    private String mealType; // breakfast, lunch, dinner, snack
    private Date date;
    private List<Ingredient> ingredients;	//holds the list of meals

    
    /**
     * constructor for Meal class with the specified attributes
     *
     * @param mealType the type of meal
     * @param date the date the meal was eaten
     * initializes an empty list of ingredients.
     */    public Meal(String mealType, Date date) {
        this.mealType = mealType;
        this.date = date;
        this.ingredients = new ArrayList<>();
    }

    
    //getters for variables
     
     /**
      * @return the meal type
      */
    public String getMealType() {
        return mealType;
    }

    /**
     * @return the date of the meal
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return a list of Ingredient objects
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * method to add the meal to the list of meals
     * @param ingredient the ingredient to add
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    //toString method to display meal type, date and ingredient size
    @Override
    public String toString() {
        return mealType + " on " + date + " with " + ingredients.size() + " ingredients";
    }
}
