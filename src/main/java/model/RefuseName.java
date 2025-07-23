package model;


/**
 * represents the name and description of a refuse type (non edible foods)
 */
public class RefuseName {
	
	//declare variables
    private int id;
    private String description;
    private String descriptionF; //description in French

    
    /**
     * constructor for class RefuseName with the given attribtues 
     *
     * @param id the unique id of the refuse type
     * @param description the description of the refuse type in English
     * @param descriptionF the description of the refuse type in French
     */    
    public RefuseName(int id, String description, String descriptionF) {
        this.id = id;
        this.description = description;
        this.descriptionF = descriptionF;
    }

    
    //getters for variables
     
     /**
      * @return the refuse type ID
      */
    public int getId() { return id; }
    
    /**
     * @return the English description
     */
    public String getDescription() { return description; }
    
    /**
     * @return the French description
     */
    public String getDescriptionF() { return descriptionF; }
}
