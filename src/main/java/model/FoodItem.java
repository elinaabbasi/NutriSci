package model;

import java.sql.Date;


/**
 * 
 * The FoodItem class holds a single food entry within the food database
 *
 */
public class FoodItem {
	
	//declare variables
    private int id;
    private String code;
    private int foodGroupId;
    private int foodSourceId;
    private String description;
    private String descriptionFrench;
    private Date dateEntry;
    private Date datePublication;
    private String countryCode;
    private String scientificName;

    /**
     * constructor for a new FoodItem with the specified parameters
     *
     * @param id the unique id of the food item
     * @param code the code associated with the food item
     * @param foodGroupId the id of the food group this item belongs to
     * @param foodSourceId the id of the food's source 
     * @param description the English description
     * @param descriptionFrench the French description
     * @param dateEntry the date this food item was entered into the database
     * @param datePublication the date the item was published 
     * @param countryCode the code of the country
     * @param scientificName the scientific name of the food item
     */
    public FoodItem(int id, String code, int foodGroupId, int foodSourceId, String description, String descriptionFrench,
                    Date dateEntry, Date datePublication, String countryCode, String scientificName) {
        this.id = id;
        this.code = code;
        this.foodGroupId = foodGroupId;
        this.foodSourceId = foodSourceId;
        this.description = description;
        this.descriptionFrench = descriptionFrench;
        this.dateEntry = dateEntry;
        this.datePublication = datePublication;
        this.countryCode = countryCode;
        this.scientificName = scientificName;
    }

    
    //getters for variables
    /**
     * @return the food item ID
     */
    public int getId() { return id; }
    
    /**
     * @return the food item code
     */
    public String getCode() { return code; }
    
    /**
     * @return the food group ID
     */
    public int getFoodGroupId() { return foodGroupId; }
    
    /**
     * @return the food source ID
     */
    public int getFoodSourceId() { return foodSourceId; }
    
    /**
     * @return the English description
     */
    public String getDescription() { return description; }
    
    /**
     * @return the French description
     */
    public String getDescriptionFrench() { return descriptionFrench; }
    
    /**
     * @return the date the food item was entered
     */
    public Date getDateEntry() { return dateEntry; }
    
    /**
     * @return the date the food item was published
     */
    public Date getDatePublication() { return datePublication; }
    
    /**
     * @return the country code
     */
    public String getCountryCode() { return countryCode; }
    
    /**
     * @return the scientific name
     */
    public String getScientificName() { return scientificName; }

    //toString method
    @Override
    public String toString() {
        return description;
    }
}
