package view;

import controller.SwapEngine;

import database.DBConnection;
import model.SwapGoal;
import model.UserProfile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

//Use of AI: Used AI to debug code and improve operations

//decided last minute not to use this panel

/**
 * A gui panel that applies nutrient based food swaps across a user's recent meals
 */

public class SwapApplierPanel extends JPanel {

    private final UserProfile user;

    private JComboBox<String> nutrientBox, directionBox, daysBox;
    private JTextField amountField;
    private JButton applyButton;
    private JTextArea resultArea;
    private JPanel chartContainer;

    /**
     * constructor for the SwapApplierPanel class for the given user profile
     *
     * @param userProfile the user whose meal examined
     */

    public SwapApplierPanel(UserProfile userProfile) {
        this.user = userProfile;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(" Apply Swaps Across Meals"));

        // controls
        JPanel controls = new JPanel(new GridLayout(2, 4, 10, 10));
        nutrientBox = new JComboBox<>(new String[]{"Energy", "Protein", "Fat", "Carbohydrate", "Fiber", "Sugar"});
        directionBox = new JComboBox<>(new String[]{"increase", "decrease"});
        daysBox = new JComboBox<>(new String[]{"7", "30"});
        amountField = new JTextField();
        applyButton = new JButton("Apply Smart Swap");

        controls.add(new JLabel("Nutrient:"));
        controls.add(nutrientBox);
        controls.add(new JLabel("Direction:"));
        controls.add(directionBox);
        controls.add(new JLabel("Amount:"));
        controls.add(amountField);
        controls.add(new JLabel("Days Back:"));
        controls.add(daysBox);

        add(controls, BorderLayout.NORTH);

        // output area
        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // chart
        chartContainer = new JPanel(new BorderLayout());
        add(chartContainer, BorderLayout.SOUTH);

        applyButton.addActionListener(e -> applySwaps());
        add(applyButton, BorderLayout.WEST);
    }

    
    /**
     * this method gets recent meals from the database, calculates the current nutrient total,
     * uses SwapEngine to find better options, updates the database with swapped food IDs,
     * displays a comparison chart
     */

    private void applySwaps() {
        String nutrient = (String) nutrientBox.getSelectedItem();
        String dir = (String) directionBox.getSelectedItem();
        int days = Integer.parseInt((String) daysBox.getSelectedItem());
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter valid amount.");
            return;
        }

        SwapGoal goal = new SwapGoal(nutrient, dir, amount);

        int swapCount = 0;
        double totalBefore = 0, totalAfter = 0;

        //sql query
        String fetchSql = """
            SELECT mi.id, mi.food_id, mi.quantity_grams
            FROM meal_ingredient mi
            JOIN logged_meal lm ON mi.meal_id = lm.id
            JOIN nutrient_amount na ON mi.food_id = na.food_id
            JOIN nutrient n ON na.nutrient_id = n.id
            WHERE n.name = ? AND lm.user_name = ? AND mi.was_swapped = 0
                  AND lm.meal_date >= CURDATE() - INTERVAL ? DAY
        """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(fetchSql)) {

            stmt.setString(1, nutrient);
            stmt.setString(2, user.getName());
            stmt.setInt(3, days);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int rowId = rs.getInt("id");
                int foodId = rs.getInt("food_id");
                double qty = rs.getDouble("quantity_grams");

                double nutrientPer100g = getNutrientAmount(foodId, nutrient);
                double before = nutrientPer100g * qty / 100;

                String replacement = SwapEngine.suggestSwap(foodId, goal);
                if (replacement != null) {
                    int newFoodId = getFoodIdByName(replacement);
                    double after = getNutrientAmount(newFoodId, nutrient) * qty / 100;

                    totalBefore += before;
                    totalAfter += after;
                    swapCount++;

                    // apply swap
                    PreparedStatement update = conn.prepareStatement(
                            "UPDATE meal_ingredient SET food_id = ?, was_swapped = 1 WHERE id = ?");
                    update.setInt(1, newFoodId);
                    update.setInt(2, rowId);
                    update.executeUpdate();

                    resultArea.append(" Swapped food ID " + foodId + " with " + newFoodId + " (" + replacement + ")\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, " Error applying swaps.");
            return;
        }

        resultArea.append("\nTotal swaps applied: " + swapCount + "\n");
        resultArea.append("Before: " + String.format("%.2f", totalBefore) + " | After: " + String.format("%.2f", totalAfter) + "\n");

        // chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(totalBefore, "Before", nutrient);
        dataset.addValue(totalAfter, "After", nutrient);

        JFreeChart chart = ChartFactory.createBarChart(
                "Cumulative " + nutrient + " (Before vs After)",
                "Nutrient",
                "Total Amount",
                dataset
        );

        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartContainer.revalidate();
    }

    /**
     * gets the food id from the database based on its name.
     *
     * @param name the food name
     * @return the corresponding food id
     * @throws Exception if the food is not found in the database
     */
    private int getFoodIdByName(String name) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM food_name WHERE description = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
            throw new Exception("Food not found: " + name);
        }
    }

    
    /**
     * gets the amount of a specific nutrient for a given food id.
     *
     * @param foodId the food id to check
     * @param nutrient the nutrient name
     * @return the nutrient amount
     * @throws Exception if the nutrient data is not found
     */
    private double getNutrientAmount(int foodId, String nutrient) throws Exception {
        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement("""
                 SELECT na.amount
                 FROM nutrient_amount na
                 JOIN nutrient n ON na.nutrient_id = n.id
                 WHERE na.food_id = ? AND n.name = ?
             """)) {
            stmt.setInt(1, foodId);
            stmt.setString(2, nutrient);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("amount");
            return 0;
        }
    }
}
