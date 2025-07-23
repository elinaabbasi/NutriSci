package model;


/**
 * represents a yield type name
 */
public class YieldName {
	
	//declare variables
    private int id;
    private String description;
    private String descriptionF;

    
    /**
     * constructor for class YieldName with the given attributes
     *
     * @param id the unique id of the yield type
     * @param description the English description of the yield type
     * @param descriptionF the French description of the yield type
     */    
    public YieldName(int id, String description, String descriptionF) {
        this.id = id;
        this.description = description;
        this.descriptionF = descriptionF;
    }
    
    
    //getters for variables
    
    /**
     * @return the yield type id
     */
    public int getId() { return id; }
    
    /**
     * @return the English description of the yield type
     */
    public String getDescription() { return description; }
    
    /**
     * @return the French description of the yield type
     */
    public String getDescriptionF() { return descriptionF; }
}
