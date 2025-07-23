package model;

/**
 * represents the source of a nutrient
 */
public class NutrientSource {
	
	//declare variables
    private int id;		//unique id of nutrient source
    private String code;
    private String description;		//description in English
    private String descriptionF;	//description in French

    
    /**
     * constructor for class NutrientSource with the given attributes
     *
     * @param id the unique id of the nutrient source
     * @param code the code representing the nutrient source
     * @param description the description of the nutrient source in English
     * @param descriptionF the description of the nutrient source in French
     */    
    public NutrientSource(int id, String code, String description, String descriptionF) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.descriptionF = descriptionF;
    }

    //getters for variables
    
    /**
     * @return the nutrient source id
     */
    public int getId() { return id; }
    
    /**
     * @return the nutrient source code
     */
    public String getCode() { return code; }
    
    /**
     * @return the English description of the nutrient source
     */
    public String getDescription() { return description; }
    
    /**
     * @return the French description of the nutrient source
     */
    public String getDescriptionF() { return descriptionF; }
}
