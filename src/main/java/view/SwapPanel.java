package view;

import controller.SwapEngine;
import database.DBConnection;
import model.SwapGoal;
import model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

//Use of AI: Used AI to debug code and improve operations
/**
 * the SwapPanel class is a gui panel for suggesting and applying the food swaps based on
 * a users nutritional goals. the user can select a food item, a nutrient goal and get a 
 * suggestion to swap their food
 *
 * the user can choose to either make the swap one just one meal 
 * or apply the swap to all previously recorded meals in the past 30 days
 *
 *
 * communicates with the SwapEngine controller
 */
public class SwapPanel extends JPanel {

    private final UserProfile user;

    private JComboBox<String> foodBox, nutrientBox, goalTypeBox;
    private JTextField amountField;
    private JButton suggestButton;
    private JTextArea resultArea;
    private JButton applyButton;
    
    private int lastSuggestedId = -1;
    private int lastOriginalId = -1;
    
    private JTextField dateField;
    private JCheckBox applyToAllCheckBox;



    private final Map<String, Integer> foodNameToId = new HashMap<>();

    /**
     * constructor for SwapPanel class with the provided user profile
     * initializes UI components and sets up action listeners for suggestions
     *
     * @param userProfile the user profile associated with the logged in user
     */
    public SwapPanel(UserProfile userProfile) {
    	//UI setup
        this.user = userProfile;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Smart Food Swap"));

        // input form panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        foodBox = new JComboBox<>();
        nutrientBox = new JComboBox<>(new String[]{"Energy", "Protein", "Fat", "Carbohydrate", "Fiber", "Sugar"});
        goalTypeBox = new JComboBox<>(new String[]{"increase", "decrease"});
        amountField = new JTextField();
        suggestButton = new JButton("Suggest Swap");
        
        dateField = new JTextField("YYYY-MM-DD");
        applyToAllCheckBox = new JCheckBox("Apply to all meals");
        applyButton = new JButton("Apply Swap");

        inputPanel.add(new JLabel("Food Item:"));
        inputPanel.add(foodBox);
        inputPanel.add(new JLabel("Nutrient:"));
        inputPanel.add(nutrientBox);
        inputPanel.add(new JLabel("Goal Type:"));
        inputPanel.add(goalTypeBox);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        
        inputPanel.add(new JLabel("Meal Date (optional):"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(applyToAllCheckBox);
        
        inputPanel.add(new JLabel(""));
        inputPanel.add(suggestButton);
        
        inputPanel.add(applyButton);
        
        applyButton.addActionListener(e -> {
            if (lastSuggestedId == -1 || lastOriginalId == -1) {
                JOptionPane.showMessageDialog(this, "No suggested swap to apply.");
                return;
            }

            try (Connection conn = DBConnection.getInstance().getConnection()) {
                PreparedStatement stmt;
                String sql;

                if (applyToAllCheckBox.isSelected()) {
                    // apply to all meals in last 30 days
                    sql = """
                            UPDATE meal_ingredient
                            SET food_id = ?, was_swapped = TRUE
                            WHERE food_id = ? AND meal_id IN (
                                SELECT id FROM logged_meal 
                                WHERE user_name = ? 
                                AND meal_date >= CURDATE() - INTERVAL 30 DAY
                            )
                          """;
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, lastSuggestedId);
                    stmt.setInt(2, lastOriginalId);
                    stmt.setString(3, user.getName());
                } else {
                    // apply to specific date
                    String dateInput = dateField.getText().trim();
                    if (dateInput.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter a date or check 'Apply to all meals'.");
                        return;
                    }
                    sql = """
                            UPDATE meal_ingredient
                            SET food_id = ?, was_swapped = TRUE
                            WHERE food_id = ? AND meal_id IN (
                                SELECT id FROM logged_meal 
                                WHERE user_name = ? 
                                AND meal_date = ?
                            )
                          """;
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, lastSuggestedId);
                    stmt.setInt(2, lastOriginalId);
                    stmt.setString(3, user.getName());
                    stmt.setDate(4, Date.valueOf(dateInput));  // validate format
                }

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Swap applied!");
                } else {
                    JOptionPane.showMessageDialog(this, "No matching ingredient found to swap.");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to apply swap.");
            }
        });

        
        

        add(inputPanel, BorderLayout.NORTH);

        // output
        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        loadFoodNames();

        suggestButton.addActionListener(e -> suggestSwap());
    }

    
    /**
     * loads the food names and and food id's from the database into the foodBox combo box
     */
    private void loadFoodNames() {
        try (Connection conn = DBConnection.getInstance().getConnection();

             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, description FROM food_name")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("description");
                foodBox.addItem(name);
                foodNameToId.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load food names from database.");
        }
    }

    
    /**
     * suggests a food swap based on the selected food, nutrient, goal type, and amount
     * displays the suggestion and stores the id's for later
     * a comparison chart is also displayed if a valid swap is found
     */
    private void suggestSwap() {
    	
    	
        try {
            String foodName = (String) foodBox.getSelectedItem();
            if (foodName == null || amountField.getText().isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            int foodId = foodNameToId.getOrDefault(foodName, -1);
            String nutrient = (String) nutrientBox.getSelectedItem();
            String type = (String) goalTypeBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());

            SwapGoal goal = new SwapGoal(nutrient, type, amount);
            String suggestion = SwapEngine.suggestSwap(foodId, goal);

            if (suggestion != null) {
            	
            	int suggestedId = getFoodIdByName(suggestion);
            	lastSuggestedId = suggestedId;
                lastOriginalId = foodId;
                
                resultArea.setText(String.format(
                        " Suggested swap for \"%s\":\n→ Try: %s\nTo %s %.2f g of %s",
                        foodName, suggestion, type, amount, nutrient
                ));

                
                if (suggestedId != -1) {
                    showSwapChart(foodId, suggestedId);
                } else {
                    resultArea.append("\n Suggested food not found in database.");
                }

            } else {
                resultArea.setText("No suitable swap found.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred.");
        }
    }
    
    
    

    /**
     * gets the food id corresponding to the given food name.
     *
     * @param name the name of the food
     * @return the food id if found, otherwise -1
     */
    private int getFoodIdByName(String name) {
        return foodNameToId.getOrDefault(name, -1);
    }

    
    /**
     * displays a comparison chart between the original and suggested food.
     *
     * @param originalId the food id of the original item
     * @param suggestedId the food id of the suggested swap
     */
    private void showSwapChart(int originalId, int suggestedId) {
        new SwapComparisonChart(originalId, suggestedId).setVisible(true);
    }
}
