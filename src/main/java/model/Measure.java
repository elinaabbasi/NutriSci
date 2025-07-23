package model;

/**
 * The Measure class represents a unit of measurement used for food items
 *
 */
public class Measure {
	
	//declare variables
    private int id;
    private String description;
    private String descriptionF;	//description in French

    
    /**
     * constructor for class Measure with the specified attributes
     *
     * @param id the unique id for the measure
     * @param description the English description of the measure
     * @param descriptionF the French description of the measure
     */
    public Measure(int id, String description, String descriptionF) {
        this.id = id;
        this.description = description;
        this.descriptionF = descriptionF;
    }

    
    //getters for variables
    
    /**
     * @return the unique measure ID
     */
    public int getId() { return id; }
    
    /**
     * @return the English description of the measure
     */
    public String getDescription() { return description; }
    
    /**
     * @return the French description of the measure
     */
    public String getDescriptionF() { return descriptionF; }
}
