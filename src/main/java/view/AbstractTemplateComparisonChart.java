package view;

import database.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

//Use of AI: Used AI to debug code and improve operations
/**
 * AbstractTemplateComparisonChart follows the Template Method design pattern.
 * it provides a template for displaying the swap comparison chart of nutrient values between an original and swapped food item
 * 
 * this abstract class defines the structure for getting nutrient data from the database 
 * and creating a bar chart for visualization using JFreeChart. 
 * 
 * the subclasses must extend this class and specify which nutrients to focus on and how to change the chart appearance.
 * 
 * 
 */
public abstract class AbstractTemplateComparisonChart extends JFrame {
	
	protected String originalName = "Original";
	protected String swapName = "Swap";
	
	 Map<String, Double> originalNutrientMap = new HashMap<>();		//holds the map of nutrient values for the original food item
     Map<String, Double> swapNutrientMap = new HashMap<>();			// holds the map of nutrient values for the swapped food item

     
     /**
      * constructor for the class AbstractTemplateComparisonChart
      * creates a comparison chart
      *
      * @param originalFoodId the id of the original food item
      * @param suggestedFoodId the id of the swapped food item
      */
    public AbstractTemplateComparisonChart(int originalFoodId, int suggestedFoodId) {
        setTitle("Before vs After Swap Nutrients");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        // get data from database
        fetchData(originalFoodId, suggestedFoodId, dataset);

        // create chart
        createChart(dataset, originalName, swapName);
    }

    
    /**
     * this method is the Template Method to get nutrient data for the food items from the database
     * it also adds data to the dataset for the chart visualization
     *
     * @param originalFoodId the id of the original food item
     * @param suggestedFoodId the id of the swapped food item
     * @param dataset the dataset to add data with nutrient values
     */
    private void fetchData(int originalFoodId, int suggestedFoodId, DefaultCategoryDataset dataset) {
        // get nutrient data from the database
       
    	
    	originalName = "Original";
    	swapName = "Swap";

    	
    	//sql query
        String sql = """
            SELECT n.name AS nutrient_name,
                   na.value AS amount,
                   fn.id AS food_id,
                   fn.description AS food_name
            FROM nutrient_amount na
            JOIN nutrient n ON na.nutrient_id = n.id
            JOIN food_name fn ON fn.id = na.food_id
            WHERE fn.id = ? OR fn.id = ?
        """;

        Set<String> mainNutrients = getMainNutrients();

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
        	
//        	String originalName = "Original";
//            String swapName = "Swap";

            stmt.setInt(1, originalFoodId);
            stmt.setInt(2, suggestedFoodId);

            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String nutrient = rs.getString("nutrient_name");
                if (!mainNutrients.contains(nutrient)) continue;

                double amount = rs.getDouble("amount");
                int foodId = rs.getInt("food_id");
                String foodName = rs.getString("food_name");

                if (foodId == originalFoodId) {
                    originalName = foodName;
                    originalNutrientMap.put(nutrient, amount);
                } else if (foodId == suggestedFoodId) {
                    swapName = foodName;
                    swapNutrientMap.put(nutrient, amount);
                }
            }

            for (String nutrient : mainNutrients) {
                Double originalVal = originalNutrientMap.getOrDefault(nutrient, 0.0);
                Double swapVal = swapNutrientMap.getOrDefault(nutrient, 0.0);
                dataset.addValue(originalVal, originalName, nutrient);
                dataset.addValue(swapVal, swapName, nutrient);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /**
     * this abstract method must be implemented by its subclasses to specify which nutrients should be used for comparison
     * this method gets the set of nutrients that should be included in the comparison chart.
     * 
     * @return a set of nutrient names to be displayed in the chart
     */
    protected abstract Set<String> getMainNutrients();

    
    /**
     * this abstract method must be implemented by subclasses to specify how the chart 
     * should be created and customized using the dataset and food names.
     *
     * @param dataset the dataset containing nutrient values
     * @param originalName the name of the original food
     * @param swapName the name of the swapped food
     */
    protected abstract void createChart(DefaultCategoryDataset dataset, String originalName, String swapName);
}
