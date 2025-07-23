package model;


/**
 * represents the user's personal profile
 */
public class UserProfile {
	
	//declare variables
    private String name;
    private String sex; // male or female
    
    private String dateOfBirth; // yyyy-mm-dd
    
    private double height; // in cm or inches
    
    private double weight; // in kg or lbs
    
    private String unitSystem; // metric or imperial

    
    /**
     * constructor for class UserProfile with the given attributes.
     *
     * @param name the user's name
     * @param sex the user's sex/gender 
     * @param dateOfBirth the user's date of birth 
     * @param height the user's height in cm or inches
     * @param weight the user's weight in kg or lbs
     * @param unitSystem the unit system 
     */    
    public UserProfile(String name, String sex, String dateOfBirth, double height, double weight, String unitSystem) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        this.unitSystem = unitSystem;
    }

    
    //getters for variables
    
    /**
     * @return the users name
     */
    public String getName() { return name; }
    
    /**
     * @return the users sex
     */
    public String getSex() { return sex; }
    
    /**
     * @return the users date of birth
     */
    public String getDateOfBirth() { return dateOfBirth; }
    
    /**
     * @return the users height
     */
    public double getHeight() { return height; }
    
    /**
     * @return the users weight
     */
    public double getWeight() { return weight; }
    
    
    /**
     * @return the unit system
     */
    public String getUnitSystem() { return unitSystem; }

    
    //setters for variables
    
    /**
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * @param sex the sex to set
     */
    public void setSex(String sex) { this.sex = sex; }
    
    /**
     * @param dateOfBirth the dateOfBirth to set
     */
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    /**
     * @param height the height to set in cm or inches
     */
    public void setHeight(double height) { this.height = height; }
    
    /**
     * @param weight the weight to set in kg or lbs
     */
    public void setWeight(double weight) { this.weight = weight; }
    
    /**
     * @param unitSystem the unit system to set 
     */
    public void setUnitSystem(String unitSystem) { this.unitSystem = unitSystem; }

    
    //toString method to display user information
    @Override
    public String toString() {
        return "UserProfile{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", unitSystem='" + unitSystem + '\'' +
                '}';
    }
}
