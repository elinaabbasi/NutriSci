package model;


/**
 * The FoodGroup class represents a group of food items.
 *
 */
public class FoodGroup {
	
  //declare variables
	
	
	private int id;
    private String code;
    
    //english name of the food group
    private String name;
    
    //french name of the food group
    private String nameFrench;

    
    /**
     * constructor for FoodGroup with the id, code, name, and French name.
     *
     * @param id the unique id for the food group
     * @param code the short code representing the group
     * @param name the English name of the food group
     * @param nameFrench the French name of the food group
     * 
     */
    public FoodGroup(int id, String code, String name, String nameFrench) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.nameFrench = nameFrench;
    }

    //create getters for variables
    
    /**
     *
     * @return the unique id of the food group 
     */
    public int getId() { return id; }
    
    /**
     * 
     * @return the short code representing the food group
     */
    public String getCode() { return code; }
    
    /**
     * 
     * @return the english name of the food group
     */
    public String getName() { return name; }
    
    /**
     * 
     * @return the french name of the food group
     */
    public String getNameFrench() { return nameFrench; }

    //toString method to return the english name of the food group
    @Override
    public String toString() {
        return name;
    }
}
