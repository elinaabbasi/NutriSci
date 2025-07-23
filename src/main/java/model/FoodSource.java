package model;

/**
 * The FoodSource class represents the origin or source of the food item
 *
 */
public class FoodSource {
	
	//declare variables
    private int id;
    private String code;
    private String description;
    private String descriptionF;

    
    /**
     * constructor for FoodSource object with the specified attributes.
     *
     * @param id  the unique id of the food source
     * @param code the code representing the food source
     * @param description the English description of the source
     * @param descriptionF the French description of the source
     */
    public FoodSource(int id, String code, String description, String descriptionF) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.descriptionF = descriptionF;
    }

    
    //getters for variables
    
    /**
     * @return the unique food source ID
     */
    public int getId() { return id; }
    
    /**
     * @return the source code
     */
    public String getCode() { return code; }
    
    /**
     * @return the English description
     */
    public String getDescription() { return description; }
    
    /**
     * @return the French description
     */
    public String getDescriptionF() { return descriptionF; }
}
