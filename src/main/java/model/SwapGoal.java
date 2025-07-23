package model;


/**
 * represents a nutrient based goal for food replacements, such as increasing or decreasing a specific nutrient by a given amount.
 */
public class SwapGoal {
	
	//declare variables
    private String nutrient;
    private String direction; // "increase" or "decrease"
    private double amount;		//in grams

    
    /**
     * constructor for class SwapGoal with the given attributes
     *
     * @param nutrient the name of the nutrient
     * @param direction the goal direction ("increase" or "decrease")
     * @param amount the amount of change wanted (in grams)
     */    
    public SwapGoal(String nutrient, String direction, double amount) {
        this.nutrient = nutrient;
        this.direction = direction;
        this.amount = amount;
    }

    
    //getters for variables
    
    /**
     * @return the nutrient name
     */
    public String getNutrient() {
        return nutrient;
    }

    
    /**
     * @return the goal direction (increase or decrease)
     */
    public String getDirection() {
        return direction;
    }

    
    /**
     * @return the amount of change in grams
     */
    public double getAmount() {
        return amount;
    }

    
    //toString method that displays the direction, nutrient and amount
    @Override
    public String toString() {
        return direction + " " + nutrient + " by " + amount + " g";
    }
}
