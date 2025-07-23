package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * the MealBuilder class uses the Builder design pattern to
 * simplify the creation of Meal objects.
 *
 * it allows the meal building properties to be built step by step before constructing a complete Meal
 * instance using the build() method.
 *
 */
public class MealBuilder {
	
	//declare variables
    private String mealType;	//holds meal type
    private Date date;		//holds date of meal
    private List<Ingredient> ingredients = new ArrayList<>();		//holds the ingredients

    /**
     * sets the meal type
     * @param mealType the meal type (breakfast, lunch, dinner, snack)
     * @return the current MealBuilder instance
     */
    public MealBuilder setMealType(String mealType) {
        this.mealType = mealType;
        return this;
    }

    /**
     * sets the meal date
     * @param date the date the meal was eaten
     * @return the current MealBuilder instance
     */    public MealBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

     /**
      * adds an ingredient to the meal being built
      * @param ingredient the Ingredient to add
      * @return the current MealBuilder
      */    public MealBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

      /**
       * builds and returns a Meal object using the formed properties.
       * @return a fully constructed Meal object
       */
    public Meal build() {
        Meal meal = new Meal(mealType, date);  // Pass user-provided date
        for (Ingredient ingredient : ingredients) {
            meal.addIngredient(ingredient);
        }
        return meal;
    }
}