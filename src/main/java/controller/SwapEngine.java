package controller;

import database.DBConnection;

import model.SwapGoal;
import model.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;


/**
 * the SwapEngine class has static methods to suggest food swaps
 * and to optimize food choices based on the users nutrient goals.
 * 
 * it uses the database find alternate foods within the same group that better match a nutrient
 * it also finds food items that satisfy multiple conditions at the same time
 * 
 */
public class SwapEngine {
	
	
	//declare a map of nutrients to map user friendly nutrient names to their respective database names
	private static final Map<String, String> nutrientMap = Map.of(
		    "Energy", "ENERGY (KILOCALORIES)",
		    "Protein", "PROTEIN",
		    "Fat", "FAT (TOTAL LIPIDS)",
		    "Carbohydrate", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)",
		    "Fiber", "FIBRE, TOTAL DIETARY",
		    "Sugar", "SUGARS, TOTAL"
		);
	

    /**
     * this method suggests a better food item from the same group based on a nutrient goal.
     * excludes the original food from the result.
     *
     * @param foodId the ID of the original food
     * @param goal the SwapGoal (nutrient, increase/decrease, amount)
     * @return suggested food name or null
     */
    public static String suggestSwap(int foodId, SwapGoal goal) {
        try (Connection conn = DBConnection.getInstance().getConnection()) {

            // 1. get the food group and original nutrient amount
            String foodGroupSql = """
                SELECT fg.id AS group_id, na.value AS original_amount
                FROM food_name fn
                JOIN food_group fg ON fn.food_group_id = fg.id
                JOIN nutrient_amount na ON fn.id = na.food_id
                JOIN nutrient n ON na.nutrient_id = n.id
                WHERE fn.id = ? AND n.name = ?
            """;

            PreparedStatement groupStmt = conn.prepareStatement(foodGroupSql);
            groupStmt.setInt(1, foodId);
            
            
            String dbNutrientName = nutrientMap.get(goal.getNutrient());
            System.out.println("Nutrient selected: " + goal.getNutrient());
            System.out.println("Mapped DB nutrient: " + dbNutrientName);
            if (dbNutrientName == null) return null;
            groupStmt.setString(2, dbNutrientName);
            
            
            ResultSet rs1 = groupStmt.executeQuery();

            if (!rs1.next()) return null;

            int groupId = rs1.getInt("group_id");
            double originalAmount = rs1.getDouble("original_amount");

            // 2. define and calculate swap goal and nutrient goal
            //direction is the goal in which the user decides
            String direction = goal.getDirection().equalsIgnoreCase("increase") ? ">" : "<";
            double target = goal.getDirection().equalsIgnoreCase("increase")
                    ? originalAmount + goal.getAmount()
                    : originalAmount - goal.getAmount();

            // 3. find the best match in same group, excluding the original food
            String swapSql =
                    "SELECT fn.description, na.value " +
                            "FROM food_name fn " +
                            "JOIN food_group fg ON fn.food_group_id = fg.id " +
                            "JOIN nutrient_amount na ON fn.id = na.food_id " +
                            "JOIN nutrient n ON na.nutrient_id = n.id " +
                            "WHERE fg.id = ? AND n.name = ? AND na.value " + direction + " ? AND fn.id != ? " +
                            "ORDER BY ABS(na.value - ?) ASC " +
                            "LIMIT 1";


            PreparedStatement swapStmt = conn.prepareStatement(swapSql);
            swapStmt.setInt(1, groupId);
            
            swapStmt.setString(2, dbNutrientName);
            swapStmt.setDouble(3, target);
            swapStmt.setInt(4, foodId);          // exclude original food
            swapStmt.setDouble(5, target);       // sort by closeness to target

            ResultSet rs2 = swapStmt.executeQuery();

            if (rs2.next()) {
                return rs2.getString("description"); //return the best matching food
            }

        } catch (Exception e) {
            e.printStackTrace(); //log error
        }

        return null; //no swap found or an error
    }
    
    

    /**
     * finds foods that satisfy all user defined nutrient goals.
     * @param goals is the list of SwapGoal objects (nutrient + direction(goal) + amount)
     * @return formatted result string of matching food suggestions. Or, if none are found, a message saying no matches are found 
     */
    public static String optimizeGoals(List<SwapGoal> goals, UserProfile user) {
        if (goals == null || goals.isEmpty()) return null;

        StringBuilder result = new StringBuilder();
        result.append("Optimizing for ").append(user.getName()).append(":\n");
       
        for (SwapGoal g : goals) {
            result.append(" • ").append(g).append("\n");
        }

        //sql query
        StringBuilder sql = new StringBuilder("""
        SELECT fn.description, fg.name AS group_name
        FROM food_name fn
        JOIN food_group fg ON fn.food_group_id = fg.id
        WHERE fn.id IN (
            SELECT food_id
            FROM nutrient_amount na
            JOIN nutrient n ON na.nutrient_id = n.id
            WHERE
    """);

        for (int i = 0; i < goals.size(); i++) {
            if (i > 0) sql.append(" OR ");
            SwapGoal goal = goals.get(i);
            String dir = goal.getDirection().equalsIgnoreCase("increase") ? ">" : "<";
            sql.append("(n.name = ? AND na.value ").append(dir).append(" ?)");
        }

        //make sure only foods matching all the nutrient conditions are returned
        sql.append(" GROUP BY food_id HAVING COUNT(DISTINCT n.name) = ").append(goals.size()).append(")");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int i = 1;
           
            
            for (SwapGoal g : goals) {
                String dbName = nutrientMap.get(g.getNutrient());
                if (dbName == null) return "Unknown nutrient: " + g.getNutrient();
                stmt.setString(i++, dbName);
                stmt.setDouble(i++, g.getAmount());
            }

            ResultSet rs = stmt.executeQuery();
            int count = 0;

            //collect the results
            while (rs.next()) {
                count++;
                result.append("\n Candidate ").append(count).append(":\n");
                result.append(" • ").append(rs.getString("description")).append("\n");
                result.append(" • Group: ").append(rs.getString("group_name")).append("\n");
            }

            if (count == 0) return "No food found satisfying all goals.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error while optimizing.";
        }

        return result.toString();
    }

}
