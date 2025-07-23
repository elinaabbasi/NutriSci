package controller;

import database.DBConnection;

import model.Ingredient;
import model.Meal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * the MealLogger class has static methods to save and retrieve meal data to and from the database for a user.
 */
public class MealLogger {

	
	/**
     * This method saves a meal and its ingredients for the specific user to the database.
     *
     * @param userName which is a String for the name of the user saving the meal
     * @param meal which is of type Meal object that is to be saved, including its ingredients, date and type
     */
    public static void saveMeal(String userName, Meal meal) {
        try (Connection conn = DBConnection.getInstance().getConnection();
        																	) {
            // insert the meal into logged_meal
            String insertMeal = "INSERT INTO logged_meal (user_name, meal_type, meal_date) VALUES (?, ?, ?)";
            PreparedStatement mealStmt = conn.prepareStatement(insertMeal, PreparedStatement.RETURN_GENERATED_KEYS);
            mealStmt.setString(1, userName);
            mealStmt.setString(2, meal.getMealType());
            mealStmt.setDate(3, new Date(meal.getDate().getTime()));
            mealStmt.executeUpdate();

            // get the meal_id
            ResultSet rs = mealStmt.getGeneratedKeys();
            int mealId = -1;
            if (rs.next()) {
                mealId = rs.getInt(1);
            }

            mealStmt.close();

            // insert each ingredient
            String insertIngredient = "INSERT INTO meal_ingredient (meal_id, food_id, quantity_grams) VALUES (?, ?, ?)";
            PreparedStatement ingStmt = conn.prepareStatement(insertIngredient);

            for (Ingredient ing : meal.getIngredients()) {
                ingStmt.setInt(1, mealId);
                ingStmt.setInt(2, ing.getFoodId());
                ingStmt.setDouble(3, ing.getQuantityInGrams());
                ingStmt.executeUpdate();
            }

            ingStmt.close();
            System.out.println("Meal and ingredients saved to database.");

            //catch exception if anything goes wrong
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * this method gets the meal and its ingredients for the user, meal type and date
     *
     * @param userName the name of the user
     * @param mealType the type of meal such as "Breakfast", "Lunch", "Dinner", "Snack"
     * @param mealDate the date of the meal
     * @return a Meal object with its respective ingredients, or it returns an empty meal if no ingredients are found
     */
    public static Meal getMeal(String userName, String mealType, java.sql.Date mealDate) {
        Meal meal = new Meal(mealType, mealDate);

        
        //sql query
        String query = """
            SELECT mi.food_id, fn.description, mi.quantity_grams
            FROM meal_ingredient mi
            JOIN logged_meal lm ON mi.meal_id = lm.id
            JOIN food_name fn ON mi.food_id = fn.id
            WHERE lm.user_name = ? AND lm.meal_type = ? AND lm.meal_date = ?
        """;

        //connect to database
        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userName);
            stmt.setString(2, mealType);
            stmt.setDate(3, mealDate);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int foodId = rs.getInt("food_id");
                String description = rs.getString("description");
                double quantity = rs.getDouble("quantity_grams");

                meal.addIngredient(new Ingredient(foodId, description, quantity));
            }

            //catch excpetion and print corresponding message
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meal;
    }


}
