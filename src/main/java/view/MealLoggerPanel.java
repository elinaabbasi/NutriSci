package view;

import model.Meal;
import model.Ingredient;
import model.UserProfile;
import utils.NutrientCalculator;
import controller.MealLogger;
import model.MealBuilder;  // Import MealBuilder

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import database.DBConnection;

//Use of AI: Used AI to debug code and improve operations
/**
 * the MealLoggerPanel class provides a UI for logging meals
 *
 * this class uses the MealLogger, NutrientCalculator and MealBuilder classes
 * to process, store, and visualize meal info 
 */
public class MealLoggerPanel extends JPanel {
    private JTextField userField, quantityField;
    private JComboBox<String> mealTypeBox, foodListBox;
    private JTextArea logOutput;
    private JButton addButton, logMealButton;
    private JTextField dateField;
    private final java.util.Set<String> expandedMeals = new java.util.HashSet<>();
    private Map<String, Integer> foodNameToId = new HashMap<>();
    private List<Ingredient> ingredients = new ArrayList<>();
    private final UserProfile user;

    /**
     * constructor for class MealLoggerPanel
     * this constructor:
     * 1. allows users to select and add food ingredients from the database
     * 2. log a meal with the selected ingredients, type and date
     * 3. calculate and display the total calories and nutrients
     * 4. view a nutrient pie chart visualization after logging their meal
     * 5. double click a meal entry to expand and view its nutrient breakdown
     * 
     * @param userProfile the user whos logging the meal
     */
    public MealLoggerPanel(UserProfile userProfile) {
        this.user = userProfile;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Meal Logger"));

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        userField = new JTextField(user.getName());
        userField.setEditable(false);
        mealTypeBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        foodListBox = new JComboBox<>();
        quantityField = new JTextField();
        addButton = new JButton("Add Ingredient");
        dateField = new JTextField("YYYY-MM-DD");  // format of date entry

        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(userField);

        inputPanel.add(new JLabel("Meal Date (YYYY-MM-DD):"));
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Meal Type:"));
        inputPanel.add(mealTypeBox);
        inputPanel.add(new JLabel("Food Item:"));
        inputPanel.add(foodListBox);
        inputPanel.add(new JLabel("Quantity (grams):"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        logOutput = new JTextArea(12, 40);
        logOutput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int offset = logOutput.viewToModel2D(e.getPoint());
                    try {
                        int line = logOutput.getLineOfOffset(offset);
                        int start = logOutput.getLineStartOffset(line);
                        int end = logOutput.getLineEndOffset(line);
                        String selectedLine = logOutput.getText().substring(start, end).trim();

                        // skip if already expanded
                        if (expandedMeals.contains(selectedLine)) {
                            return;
                        }

                        if (selectedLine.contains(" on ") && selectedLine.contains(" - ")) {
                            String[] parts = selectedLine.split(" on | - ");
                            String mealType = parts[0].trim();
                            String dateStr = parts[1].trim();

                            java.sql.Date mealDate = java.sql.Date.valueOf(dateStr);
                            Meal selectedMeal = MealLogger.getMeal(user.getName(), mealType, mealDate);
                            Map<String, Double> nutrients = NutrientCalculator.calculateTotalNutrients(selectedMeal.getIngredients());

                            StringBuilder breakdown = new StringBuilder();
                            breakdown.append("\nNutrient Breakdown:\n");
                            for (Map.Entry<String, Double> entry : nutrients.entrySet()) {
                                breakdown.append("   • ").append(entry.getKey())
                                         .append(": ").append(String.format("%.2f", entry.getValue())).append("\n");
                            }

                            String existingText = logOutput.getText();
                            String[] lines = existingText.split("\n");

                            StringBuilder newText = new StringBuilder();
                            for (int i = 0; i < lines.length; i++) {
                                newText.append(lines[i]).append("\n");
                                if (i == line) {
                                    newText.append(breakdown);
                                }
                            }

                            logOutput.setText(newText.toString());
                            logOutput.append("\n");

                            expandedMeals.add(selectedLine);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        logOutput.setEditable(false);
        add(new JScrollPane(logOutput), BorderLayout.CENTER);

        logMealButton = new JButton("Log Meal");
        add(logMealButton, BorderLayout.SOUTH);

        loadFoodItemsFromDB();

        addButton.addActionListener(e -> {
            try {
                String foodName = (String) foodListBox.getSelectedItem();
                int foodId = foodNameToId.get(foodName);
                double qty = Double.parseDouble(quantityField.getText());

                Ingredient ing = new Ingredient(foodId, foodName, qty);
                ingredients.add(ing);
                logOutput.append("➕ " + ing + "\n");

                quantityField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please select a valid food and quantity.");
            }
        });

        // log meal action listener
        logMealButton.addActionListener(e -> {
            if (ingredients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one ingredient.");
                return;
            }

            String dateStr = dateField.getText().trim();
            java.sql.Date mealDate;
            try {
                mealDate = java.sql.Date.valueOf(dateStr); // expects "YYYY-MM-DD"
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }

            String mealType = mealTypeBox.getSelectedItem().toString();

            // only restrict if meal type is not a Snack
            if (!mealType.equalsIgnoreCase("Snack")) {
                if (isMealAlreadyLogged(user.getName(), mealType, mealDate)) {
                    JOptionPane.showMessageDialog(this,
                        "You’ve already logged " + mealType + " for " + mealDate + ". You can only log one " + mealType + " per day.");
                    return;
                }
            }

            // use MealBuilder to construct the meal
            MealBuilder builder = new MealBuilder();
            builder.setMealType(mealTypeBox.getSelectedItem().toString())
                   .setDate(mealDate);

            for (Ingredient ing : ingredients) {
                builder.addIngredient(ing);
            }

            Meal meal = builder.build();
            MealLogger.saveMeal(user.getName(), meal);

            logOutput.append("\nMeal logged: " + meal + "\n");

            Map<String, Double> nutrients = NutrientCalculator.calculateTotalNutrients(meal.getIngredients());
            
            double totalCalories = 0;
            for (Ingredient ing : meal.getIngredients()) {
                totalCalories += NutrientCalculator.getCaloriesForFood(ing.getFoodId(), ing.getQuantityInGrams());
            }

            String mealSummary = mealType + " on " + mealDate + " - Total Calories: " + String.format("%.2f", totalCalories) + " kcal. Double click here to view nutrient breakdown";
            logOutput.append(mealSummary + "\n");

            String title = meal.getMealType() + " Nutrients for " + user.getName();
            new NutrientPieChart(nutrients, title).setVisible(true);

            ingredients.clear();
        });
    }

    /**
     * loads food item names and their ids from the database
     * the names are added to the foodListBox combo box for the user selection
     */
    private void loadFoodItemsFromDB() {
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, description FROM food_name")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("description");
                foodListBox.addItem(name);
                foodNameToId.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * checks whether a meal of the specified type has already been logged by the user on the given date.
     * this is used to make sure that only one Breakfast, Lunch, or Dinner
     * can be logged per day for a user (Snacks can be logged multiple times)
     *
     * @param userName the name of the user
     * @param mealType the type of meal 
     * @param mealDate the date the meal is being logged for
     * @return true if a meal of the same type is already logged on that date. Otherwise, false
     */
    private boolean isMealAlreadyLogged(String userName, String mealType, Date mealDate) {
        String query = "SELECT COUNT(*) FROM logged_meal WHERE user_name = ? AND meal_type = ? AND meal_date = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userName);
            stmt.setString(2, mealType);
            stmt.setDate(3, new java.sql.Date(mealDate.getTime()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
