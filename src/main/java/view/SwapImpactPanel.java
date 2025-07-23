package view;

import database.DBConnection;
import model.UserProfile;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

//Use of AI: Used AI to debug code, create SQL statements, and improve operations
/**
 * a gui panel that visualizes the impact of food swaps on nutrient intake
 * over a user defined time period. it allows selection of a nutrient, time range,
 * and chart type, then displays a comparison of original vs. swapped foods.
 */
public class SwapImpactPanel extends JPanel {
    
	/** Maps user friendly nutrient names to their database names. */
    private static final Map<String, String> nutrientMap = Map.of(
            "Energy", "ENERGY (KILOCALORIES)",
            "Protein", "PROTEIN",
            "Fat", "FAT (TOTAL LIPIDS)",
            "Carbohydrate", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)",
            "Fiber", "FIBRE, TOTAL DIETARY",
            "Sugar", "SUGARS, TOTAL"
    );

    private final UserProfile user;
    private JComboBox<String> nutrientBox, daysBox, chartTypeBox;
    private JButton loadButton;
    private JPanel chartContainer;

    /**
     * constructor for SwapImpactPanel class for a given user profile.
     *
     * @param userProfile the UserProfile that will display its nutrient swap data
     */
    public SwapImpactPanel(UserProfile userProfile) {
        this.user = userProfile;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(" Swap Impact Visualizer"));

        JPanel controls = new JPanel(new GridLayout(1, 5, 10, 10));
        nutrientBox = new JComboBox<>(new String[]{"Energy", "Protein", "Fat", "Carbohydrate", "Fiber", "Sugar"});
        daysBox = new JComboBox<>(new String[]{"7", "14", "30"});
        chartTypeBox = new JComboBox<>(new String[]{"Line Chart", "Bar Chart"});
        loadButton = new JButton("Show Chart");

        controls.add(new JLabel("Nutrient:"));
        controls.add(nutrientBox);
        controls.add(new JLabel("Days:"));
        controls.add(daysBox);
        controls.add(new JLabel("Chart Type:"));
        controls.add(chartTypeBox);
        controls.add(loadButton);

        add(controls, BorderLayout.NORTH);

        chartContainer = new JPanel(new BorderLayout());
        add(chartContainer, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadChart());
    }

    
    /**
     * loads and creates the nutrient chart based on the options the user chooses 
     * gets the nutrient totals for original and swapped meals from the database,
     * calculates them by day, and displays a comparison chart.
     */
    private void loadChart() {
        String nutrient = (String) nutrientBox.getSelectedItem();
        String dbNutrient = nutrientMap.get(nutrient);
        if (dbNutrient == null) {
            JOptionPane.showMessageDialog(this, "Unknown nutrient selected.");
            return;
        }

        int days = Integer.parseInt((String) daysBox.getSelectedItem());
        Map<String, Double> normal = new TreeMap<>();
        Map<String, Double> swapped = new TreeMap<>();

        //sql query
        String sql = """
            SELECT DATE(lm.meal_date) AS day,
                   SUM(na.value) AS total,
                   mi.was_swapped
            FROM logged_meal lm
            JOIN meal_ingredient mi ON lm.id = mi.meal_id
            JOIN nutrient_amount na ON mi.food_id = na.food_id
            JOIN nutrient n ON na.nutrient_id = n.id
            WHERE n.name = ? AND lm.user_name = ? AND lm.meal_date >= CURDATE() - INTERVAL ? DAY
            GROUP BY day, mi.was_swapped
        """;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dbNutrient);
            stmt.setString(2, user.getName());
            stmt.setInt(3, days);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("day");
                boolean wasSwapped = rs.getBoolean("was_swapped");
                double val = rs.getDouble("total");

                if (wasSwapped) {
                    swapped.put(day, swapped.getOrDefault(day, 0.0) + val);
                } else {
                    normal.put(day, normal.getOrDefault(day, 0.0) + val);
                }
            }

            //handle exceptions if chart cant be loaded or nutrient cant be found
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Failed to load chart data.");
            return;
        }

        if (normal.isEmpty() && swapped.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ No nutrient data found for this period.");
            return;
        }

        Set<String> allDays = new TreeSet<>();
        allDays.addAll(normal.keySet());
        allDays.addAll(swapped.keySet());

        String chartType = (String) chartTypeBox.getSelectedItem();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String day : allDays) {
            dataset.addValue(normal.getOrDefault(day, 0.0), "Original", day);
            dataset.addValue(swapped.getOrDefault(day, 0.0), "Swapped", day);
        }

        // Use factory to create the chart
        CustomChartFactory factory = CustomChartFactory.getFactory(chartType);
        JFreeChart chart = factory.createChart(nutrient, user.getName(), dataset);
        
        //to make bar graph pink if wanted
//        if (chart.getPlot() instanceof org.jfree.chart.plot.CategoryPlot categoryPlot
//                && categoryPlot.getRenderer() instanceof org.jfree.chart.renderer.category.BarRenderer barRenderer) {
//
//            barRenderer.setSeriesPaint(0, Color.PINK); // "Original"
//            barRenderer.setSeriesPaint(1, new Color(100, 149, 237)); // "Swapped" = Cornflower Blue
//        }

        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartContainer.revalidate();
    }
}
