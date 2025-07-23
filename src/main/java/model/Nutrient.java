package model;


/**
 * The Nutrient class represents a nutrient entity such as protein or fat etc
 *
 */
public class Nutrient {
	
	//declare variables
    private int id;
    private String code;
    private String symbol;
    private String unit;
    private String name;
    private String nameFrench;
    private String tagname;
    private int decimals;

    /**
     * constructor for class Nutrient with the given parameters
     *
     * @param id the unique id of the nutrient
     * @param code the code representing the nutrient
     * @param symbol the symbol for the nutrient
     * @param unit the unit of measurement
     * @param name the English name of the nutrient
     * @param nameFrench the French name of the nutrient
     * @param tagname a tag used for mapping 
     * @param decimals a number of decimal places for formatted display
     */
    public Nutrient(int id, String code, String symbol, String unit, String name,
                    String nameFrench, String tagname, int decimals) {
        this.id = id;
        this.code = code;
        this.symbol = symbol;
        this.unit = unit;
        this.name = name;
        this.nameFrench = nameFrench;
        this.tagname = tagname;
        this.decimals = decimals;
    }

    
    //getters for Nutrient
    
    /**
     * @return the nutrient id
     */
    public int getId() { return id; }
    
    /**
     * @return the nutrient code
     */
    public String getCode() { return code; }
    
    /**
     * @return the nutrient symbol
     */
    public String getSymbol() { return symbol; 
    
    /**
     * @return the nutrient measurement unit
     */}
    public String getUnit() { return unit; }
    
    
    /**
     * @return the English name of the nutrient
     */
    public String getName() { return name; }
    
    
    /**
     * @return the French name of the nutrient
     */
    public String getNameFrench() { return nameFrench; }
    
    
    /**
     * @return the tag name for mapping
     */
    public String getTagname() { return tagname; }
    
    
    /**
     * @return the number of decimal places for formatting
     */
    public int getDecimals() { return decimals; }
}
