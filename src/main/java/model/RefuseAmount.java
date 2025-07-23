package model;


/**
 * represents the amount of refuse (non edible foods) associated with a specific food item.
 */
public class RefuseAmount {
	
	//declare variables
    private int foodId;
    private int refuseId;
    private double amount;
    private String dateOfEntry;

    
    /**
     * constructor for class RefuseAmount with the given attributes
     *
     * @param foodId the id of the food item
     * @param refuseId the id of the refuse type
     * @param amount the amount of refuse
     * @param dateOfEntry the date this data was entered
     */    public RefuseAmount(int foodId, int refuseId, double amount, String dateOfEntry) {
        this.foodId = foodId;
        this.refuseId = refuseId;
        this.amount = amount;
        this.dateOfEntry = dateOfEntry;
    }

    
    //getters for variables
     
     /**
      * @return the food ID
      */
    public int getFoodId() { return foodId; }
    
    
    /**
     * @return the refuse ID
     */
    public int getRefuseId() { return refuseId; }
    
    
    /**
     *
     * @return the amount of refuse
     */
    public double getAmount() { return amount; }
    
    
    /**
     * @return the date the data was entered as a String
     */
    public String getDateOfEntry() { return dateOfEntry; }
}
