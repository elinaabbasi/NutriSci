package utils;

/**
 * utility class for classifying food groups according to Canada's Food Guide.
 */
public class CFGClassifier {
	
	/**
	 * @param foodGroupId the id representing a food group
     * @return the corresponding category name as a String
	 */
    public static String classify(int foodGroupId) {
        return switch (foodGroupId) {
            case 1, 2, 3, 9, 12 -> "Vegetables & Fruits";
            case 4, 10          -> "Whole Grains";
            case 5, 6, 7, 8     -> "Protein Foods";
            default             -> "Other";
        };
    }
}
