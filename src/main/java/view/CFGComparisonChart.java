package view;

import database.DBConnection;
import model.UserProfile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import utils.CFGClassifier;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

//Use of AI: Used AI to debug code and improve operations
/**
 * displays a bar chart comparing the user's actual food intake proportions
 * to the recommended proportions in the Canada's Food Guide
 *
 *
 */
public class CFGComparisonChart extends JFrame {

	
	/**
     * constructor for the class CFGComparisonChart
     * 
     * it gets the user's meal, calculates the total intake percentage 
     * for each CFG category, compares them to ideal targets, and displays a 
     * bar chart showing both the actual and ideal percentages
     *
     * @param user the user whose food intake will be compared against the CFG
     */
    public CFGComparisonChart(UserProfile user) {
        setTitle("CFG Comparison Chart");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Map<String, Double> actual = new HashMap<>();
        double totalGrams = 0.0;

        String sql = """
            SELECT fg.id, SUM(mi.quantity_grams) AS total_grams
            FROM logged_meal lm
            JOIN meal_ingredient mi ON mi.meal_id = lm.id
            JOIN food_name fn ON mi.food_id = fn.id
            JOIN food_group fg ON fn.food_group_id = fg.id
            WHERE lm.user_name = ?
            GROUP BY fg.id;
        """;

        //get the users total intake per food group
        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int groupId = rs.getInt("id");
                double grams = rs.getDouble("total_grams");
                totalGrams += grams;

                //seperate the food groups according to the CFG
                String category = CFGClassifier.classify(groupId);
                actual.put(category, actual.getOrDefault(category, 0.0) + grams);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load data.");
            return;
        }

        
        //handle case for empty data
        if (totalGrams == 0.0) {
            JOptionPane.showMessageDialog(this, "No meal data found for " + user.getName());
            return;
        }

        // convert the original values to percentages
        Map<String, Double> actualPct = new HashMap<>();
        for (Map.Entry<String, Double> entry : actual.entrySet()) {
            actualPct.put(entry.getKey(), (entry.getValue() / totalGrams) * 100);
        }

        // ideal CFG targets
        Map<String, Double> ideal = new HashMap<>();
        ideal.put("Vegetables & Fruits", 50.0);
        ideal.put("Whole Grains", 25.0);
        ideal.put("Protein Foods", 25.0);

        // create dataset for the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String group : ideal.keySet()) {
            dataset.addValue(ideal.get(group), "CFG Ideal %", group);
            dataset.addValue(actualPct.getOrDefault(group, 0.0), "Your Plate %", group);
        }

        
        //create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Canada Food Guide Alignment",
                "Food Group",
                "Percentage (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        //display chart in window
        setContentPane(new ChartPanel(chart));
    }
}
