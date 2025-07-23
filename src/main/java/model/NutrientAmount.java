package model;

import java.sql.Date;

/**
 * 
 *The NutrientAmount class represents the amount of a specific nutrient found in a given food item
 */
public class NutrientAmount {
	
	//declare variables
    private int foodId;
    private int nutrientId;
    private double value;
    private double stdError;
    private int numObservations;
    private int sourceId;
    private Date dateOfEntry;

    /**
     * constructor for class NutrientAmount with the given attributes.
     *
     * @param foodId the id of the food item
     * @param nutrientId the id of the nutrient
     * @param value the measured amount of the nutrient
     * @param stdError the standard error of the value
     * @param numObservations the number of observations used to calculate the value
     * @param sourceId the id of the source
     * @param dateOfEntry the date the nutrient data was entered
     */    
    public NutrientAmount(int foodId, int nutrientId, double value, double stdError, int numObservations, int sourceId, Date dateOfEntry) {
        this.foodId = foodId;
        this.nutrientId = nutrientId;
        this.value = value;
        this.stdError = stdError;
        this.numObservations = numObservations;
        this.sourceId = sourceId;
        this.dateOfEntry = dateOfEntry;
    }

    
    //getters for variables
    
    /**
     * @return the food ID
     */
    public int getFoodId() { return foodId; }
    
    /**
     * @return the nutrient ID
     */
    public int getNutrientId() { return nutrientId; }
    
    /**
     * @return the nutrient value for the food
     */
    public double getValue() { return value; }
    
    /**
     * @return the standard error of the nutrient value
     */
    public double getStdError() { return stdError; }
    
    /**
     * @return the number of observations
     */
    public int getNumObservations() { return numObservations; }
    
    /**
     *
     * @return the source ID
     */
    public int getSourceId() { return sourceId; }
    
    
    /**
     * @return the date the nutrient info was entered 
     */
    public Date getDateOfEntry() { return dateOfEntry; }
}
