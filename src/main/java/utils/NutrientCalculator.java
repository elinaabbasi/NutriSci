package utils;

import database.DBConnection;

import model.Ingredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Use of AI: Used AI to debug code and create SQL statements
/**
 * utility class for calculating nutrient information for ingredients.
 */
public class NutrientCalculator {
	
	
	/**
     * calculates the total amount of each nutrient from a list of ingredients
     * 
     * for each ingredient, it gets the nutrient values from the database based on its food ID
     * the results are returned as a map where the key is the nutrient name and the value is the total amount
     * 
     * @param ingredients List of Ingredient objects representing the foods to examine.
     * @return A Map<String, Double> where each key is a nutrient name and the value is the total amount found.
     */
    public static Map<String, Double> calculateTotalNutrients(List<Ingredient> ingredients) {
        Map<String, Double> totals = new HashMap<>();

        String query = """
            SELECT n.name, na.value
            FROM nutrient_amount na
            JOIN nutrient n ON na.nutrient_id = n.id
            WHERE na.food_id = ?
        """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (Ingredient ing : ingredients) {
                stmt.setInt(1, ing.getFoodId());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String nutrientName = rs.getString("name");
                    double amountPer100g = rs.getDouble("value");

                    double scaledAmount = amountPer100g;
                    //double scaledAmount = amountPer100g * (ing.getQuantityInGrams() / 100.0);

                    totals.merge(nutrientName, scaledAmount, Double::sum);
                }

                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totals;
    }

    
    /**
     * calculates calories for a single food based on energy content
     * this method checks the database for the nutrient value corresponding to energy for a given food ID. 
     * if the energy value is found, it returns it.
     *
     * @param foodId the id of the food item.
     * @param quantityInGrams the quantity in grams
     * @return the energy content in kilocalories if found. Otherwise, 0.0 if not found or an error occurs.
     */
    public static double getCaloriesForFood(int foodId, double quantityInGrams) {
        String query = """
            SELECT na.value
            FROM nutrient_amount na
            JOIN nutrient n ON na.nutrient_id = n.id
            WHERE na.food_id = ? AND LOWER(n.name) LIKE '%energy%'
        """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double kcalPer100g = rs.getDouble("value");
                return kcalPer100g;
                //return kcalPer100g * (quantityInGrams / 100.0);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
