package controller;

import database.DBConnection;
import model.NutrientProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

//Use of AI: Used AI to debug code, create database queries, and improve operations
/**
 * the NutrientFetcher class has methods for getting nutritional information for a specified food item from the database. 
 * it gets both the food name and its nutrient amounts and creates a Nutrient Profile object
 */
public class NutrientFetcher {

	/**
     * this method gets the nutrient profile of a food item given its food ID
     * this method performs two things 
     * 1. it gets the food description from food_name 
     * 2. it gets the associated nutrients and its amounts from nutrient_amount
     * 
     * @param foodId the unique food ID of the food item
     * @return a NutrientProfile object containing the food name and nutrient amounts
     */
    public static NutrientProfile getNutrientProfile(int foodId) {
        NutrientProfile profile = new NutrientProfile();

        try (Connection conn = DBConnection.getInstance().getConnection();
) {
            //get the food name
            String foodSql = "SELECT description FROM food_name WHERE id = ?";
            PreparedStatement foodStmt = conn.prepareStatement(foodSql);
            foodStmt.setInt(1, foodId);
            ResultSet foodRs = foodStmt.executeQuery();
            if (foodRs.next()) {
                profile.setFoodName(foodRs.getString("description"));
            }

            //get the nutrients and their amounts
            String nutSql = """
                SELECT n.name, na.amount
                FROM nutrient_amount na
                JOIN nutrient n ON na.nutrient_id = n.id
                WHERE na.food_id = ?
            """;
            PreparedStatement nutStmt = conn.prepareStatement(nutSql);
            nutStmt.setInt(1, foodId);
            ResultSet nutRs = nutStmt.executeQuery();

            Map<String, Double> nutrients = new HashMap<>();
            while (nutRs.next()) {
                nutrients.put(nutRs.getString("name"), nutRs.getDouble("amount"));
            }

            profile.setNutrientAmounts(nutrients);
            
            //catch exception if something goes wrong
        } catch (Exception e) {
            e.printStackTrace();
        }

        return profile;
    }
}
