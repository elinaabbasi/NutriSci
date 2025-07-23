package model;


/**
 * the ConversionFactor class represents the conversion factor
 * used to convert a specific food item and measurement into a standard quantity
 *
 */
public class ConversionFactor {
   
	//declare variables
	private int foodId;
    private int measureId;
    private double factorValue;
    private String dateOfEntry;

    
    /**
     * constructor for a new ConversionFactor with the specified values.
     *
     * @param foodId the ID of the food item
     * @param measureId the ID of the measurement
     * @param factorValue the numeric conversion factor
     * @param dateOfEntry the date the factor was entered
     */
    public ConversionFactor(int foodId, int measureId, double factorValue, String dateOfEntry) {
        this.foodId = foodId;
        this.measureId = measureId;
        this.factorValue = factorValue;
        this.dateOfEntry = dateOfEntry;
    }

    
    /**
     * gets the food ID.
     *
     * @return the food ID
     */
    public int getFoodId() { return foodId; }
    
    /**
     * gets the measure ID
     * @return the measure ID
     */
    public int getMeasureId() { return measureId; }
    
    /**
     * gets the factor value
     * @return the factor value
     */
    public double getFactorValue() { return factorValue; }
    
    /**
     * gets the date of entry
     * @return the date of entry
     */
    public String getDateOfEntry() { return dateOfEntry; }
}
