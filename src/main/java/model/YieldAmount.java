package model;


/**
 * represents the yield amount of a food item
 */
public class YieldAmount {
	
	//declare variables
    private int foodId;
    private int yieldId;
    private double percentage;
    private String dateOfEntry;

    
    /**
     * constructor for class YieldAmount with the given attributes
     *
     * @param foodId the id of the food item
     * @param yieldId the id of the yield type
     * @param percentage the yield percentage
     * @param dateOfEntry the date this data was entered
     */
    public YieldAmount(int foodId, int yieldId, double percentage, String dateOfEntry) {
        this.foodId = foodId;
        this.yieldId = yieldId;
        this.percentage = percentage;
        this.dateOfEntry = dateOfEntry;
    }

    
    //getters for variables
    
    /**
     * @return the id of the food item
     */
    public int getFoodId() {
        return foodId;
    }

    
    /**
     * @return the id representing the yield type
     */
    public int getYieldId() {
        return yieldId;
    }

    /**
     * @return the percentage of yield
     */
    public double getPercentage() {
        return percentage;
    }

    
    /**
     * @return the date this data was entered
     */
    public String getDateOfEntry() {
        return dateOfEntry;
    }

    //toString method to display Yield Amount
    @Override
    public String toString() {
        return "YieldAmount{" +
                "foodId=" + foodId +
                ", yieldId=" + yieldId +
                ", percentage=" + percentage +
                ", dateOfEntry='" + dateOfEntry + '\'' +
                '}';
    }
}
