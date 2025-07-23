package database;

import model.FoodGroup;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.Arrays;


/*this class has methods for creating nutritional related database tables from the CSV Files
 * these methods make sure that the data is only inserted if the table is empty
 */

public class CNFLoader {

	
	/**
     * load food group data from the CSV file into the food_group. 
     * skips the import if the table already has data.
     *
     * @param csvFile the path to the CSV file
     */
    public static void loadFoodGroups(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM food_group";
        String insertSql = "INSERT INTO food_group (id, code, name, name_french) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("food_group table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0, failed = 0;
                String[] parts;

                while ((parts = reader.readNext()) != null) {
                    if (parts.length < 4) continue;

                    try {
                        stmt.setInt(1, Integer.parseInt(parts[0].trim()));
                        stmt.setString(2, parts[1].trim());
                        stmt.setString(3, parts[2].trim());
                        stmt.setString(4, parts[3].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + Arrays.toString(parts));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading food_group table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting food_group CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking food_group table.");
            e.printStackTrace();
        }
    }
    
    
    /**
     * loads the food item data from the CSV Files into the food_name table
     * skips the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadFoodItems(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM food_name";
        String insertSql = """
        INSERT INTO food_name (
            id, code, food_group_id, food_source_id,
            description, description_french,
            date_entry, date_publication,
            country_code, scientific_name
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("food_name table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                String[] line = reader.readNext(); // Skip header
                int inserted = 0;
                int failed = 0;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 10) continue;

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setString(2, line[1].trim());
                        stmt.setInt(3, Integer.parseInt(line[2].trim()));
                        stmt.setInt(4, Integer.parseInt(line[3].trim()));
                        stmt.setString(5, line[4].trim());
                        stmt.setString(6, line[5].trim());

                        stmt.setDate(7, isValidDate(line[6]) ? Date.valueOf(line[6].trim()) : null);
                        stmt.setDate(8, isValidDate(line[7]) ? Date.valueOf(line[7].trim()) : null);

                        String countryCode = line[8].trim();
                        stmt.setString(9, countryCode.isEmpty() || !countryCode.matches("[A-Za-z]{2,}") ? null : countryCode);
                        stmt.setString(10, line[9].trim());

                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading food_name table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception fileEx) {
                System.err.println("Error reading or inserting food_name data.");
                fileEx.printStackTrace();
            }

        } catch (Exception dbEx) {
            System.err.println("Error checking food_name table.");
            dbEx.printStackTrace();
        }
    }
    
    /**
     * load the nutrient information from the CSV files into the nutrient table
     * skips the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadNutrients(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM nutrient";
        String insertSql = """
        INSERT INTO nutrient (
            id, code, symbol, unit, name, name_french, tagname, decimals
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("nutrient table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                String[] line = reader.readNext(); // Skip header
                int inserted = 0, failed = 0;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 8 || !line[0].trim().matches("\\d+")) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setString(2, line[1].trim());
                        stmt.setString(3, line[2].trim());
                        stmt.setString(4, line[3].trim());
                        stmt.setString(5, line[4].trim());
                        stmt.setString(6, line[5].trim());
                        stmt.setString(7, line[6].trim());
                        stmt.setInt(8, line[7].trim().isEmpty() ? 0 : Integer.parseInt(line[7].trim()));
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading nutrient table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting nutrient CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking nutrient table.");
            e.printStackTrace();
        }
    }

    /**
     * load the nutrient values for the food items into the nutrient_amount table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadNutrientAmounts(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM nutrient_amount";
        String insertSql = """
        INSERT INTO nutrient_amount (
            food_id, nutrient_id, value, std_error, num_observations, source_id, date_of_entry
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("nutrient_amount table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0, failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 7) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setInt(2, Integer.parseInt(line[1].trim()));
                        stmt.setDouble(3, line[2].isEmpty() ? 0.0 : Double.parseDouble(line[2].trim()));
                        stmt.setDouble(4, line[3].isEmpty() ? 0.0 : Double.parseDouble(line[3].trim()));
                        stmt.setInt(5, line[4].isEmpty() ? 0 : Integer.parseInt(line[4].trim()));
                        stmt.setInt(6, line[5].isEmpty() ? 0 : Integer.parseInt(line[5].trim()));
                        stmt.setDate(7, line[6].isEmpty() ? null : Date.valueOf(line[6].trim()));

                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading nutrient_amount table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting nutrient_amount CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking nutrient_amount table.");
            e.printStackTrace();
        }
    }


    /**
     * load the measure names into the measure_name table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadMeasures(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM measure_name";
        String insertSql = "INSERT INTO measure_name (id, description, description_f) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("measure_name table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // skip header
                int inserted = 0;
                int failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 3) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setString(2, line[1].trim());
                        stmt.setString(3, line[2].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading measure_name table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting measure_name CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking measure_name table.");
            e.printStackTrace();
        }
    }
    
    /**
     * loads the food sources descriptions into the food_source table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadFoodSources(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM food_source";
        String insertSql = "INSERT INTO food_source (id, code, description, description_f) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("food_source table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0;
                int failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 4) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setString(2, line[1].trim());
                        stmt.setString(3, line[2].trim());
                        stmt.setString(4, line[3].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading food_source table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting food_source CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking food_source table.");
            e.printStackTrace();
        }
    }
    
    /**
     * loads the conversion factors into the conversion_factor table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadConversionFactors(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM conversion_factor";
        String insertSql = """
        INSERT INTO conversion_factor (food_id, measure_id, factor_value, date_of_entry)
        VALUES (?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("conversion_factor table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0;
                int failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 4) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setInt(2, Integer.parseInt(line[1].trim()));
                        stmt.setDouble(3, Double.parseDouble(line[2].trim()));
                        stmt.setString(4, line[3].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading conversion_factor table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting conversion_factor CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking conversion_factor table.");
            e.printStackTrace();
        }
    }
    
    /**
     * load the nutrient sources into the nutrient_source table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadNutrientSources(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM nutrient_source";
        String insertSql = "INSERT INTO nutrient_source (id, code, description, description_f) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("nutrient_source table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0;
                int failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 4) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setString(2, line[1].trim());
                        stmt.setString(3, line[2].trim());
                        stmt.setString(4, line[3].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading nutrient_source table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting nutrient_source CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking nutrient_source table.");
            e.printStackTrace();
        }
    }
    
    /**
     * loads the descriptions of inedible parts of the food into the refuse_name table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadRefuseNames(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM refuse_name";
        String insertSql = "INSERT INTO refuse_name (id, description, description_f) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("refuse_name table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0, failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 3) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setString(2, line[1].trim());
                        stmt.setString(3, line[2].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading refuse_name table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting refuse_name CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking refuse_name table.");
            e.printStackTrace();
        }
    }
    
    /**
     * loads the refuse amount for each food item into the refuse_amount table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadRefuseAmounts(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM refuse_amount";
        String insertSql = "INSERT INTO refuse_amount (food_id, refuse_id, amount, date_of_entry) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("refuse_amount table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0, failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 4) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setInt(2, Integer.parseInt(line[1].trim()));
                        stmt.setDouble(3, Double.parseDouble(line[2].trim()));
                        stmt.setString(4, line[3].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.println("Finished loading refuse_amount table.");
                System.out.println("Rows inserted: " + inserted);
                System.out.println("Rows failed: " + failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting refuse_amount CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking refuse_amount table.");
            e.printStackTrace();
        }
    }
    
    /**
     * load the yield name descriptions into the yield_name table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadYieldNames(String csvFile) {
        String checkSql = "SELECT COUNT(*) FROM yield_name";
        String insertSql = "INSERT INTO yield_name (id, description, description_f) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("yield_name table already contains data. Skipping import.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(csvFile));
                 PreparedStatement stmt = conn.prepareStatement(insertSql)) {

                reader.readNext(); // Skip header
                int inserted = 0, failed = 0;
                String[] line;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 3) {
                        failed++;
                        continue;
                    }

                    try {
                        stmt.setInt(1, Integer.parseInt(line[0].trim()));
                        stmt.setString(2, line[1].trim());
                        stmt.setString(3, line[2].trim());
                        stmt.executeUpdate();
                        inserted++;
                    } catch (Exception ex) {
                        failed++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.printf("Finished loading yield_name table. %d rows inserted, %d failed.%n", inserted, failed);

            } catch (Exception e) {
                System.err.println("Error reading or inserting yield_name CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking yield_name table.");
            e.printStackTrace();
        }
    }

    /**
     * load the yield amounts into the yield_amount table
     * skip the import if the table already has data
     * @param csvFile the path to the CSV file
     */
    public static void loadYieldAmounts(String csvPath) {
        String checkSql = "SELECT COUNT(*) FROM yield_amount";
        String insertSql = "INSERT INTO yield_amount (food_id, yield_id, amount, date_of_entry) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DBConnection.getInstance().getConnection();

                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                ResultSet rs = checkStmt.executeQuery()
        ) {
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("yield_amount table already contains data. Skipping import.");
                return;
            }

            try (
                    CSVReader reader = new CSVReader(new FileReader(csvPath));
                    PreparedStatement stmt = conn.prepareStatement(insertSql)
            ) {
                String[] line;
                reader.readNext(); // skip header
                int successCount = 0, failCount = 0;

                while ((line = reader.readNext()) != null) {
                    if (line.length < 3) {
                        failCount++;
                        continue;
                    }

                    try {
                        int foodId = Integer.parseInt(line[0].trim());
                        int yieldId = Integer.parseInt(line[1].trim());
                        double amount = Double.parseDouble(line[2].trim());
                        String date = (line.length > 3 && !line[3].trim().isEmpty()) ? line[3].trim() : null;

                        stmt.setInt(1, foodId);
                        stmt.setInt(2, yieldId);
                        stmt.setDouble(3, amount);
                        stmt.setString(4, date);

                        stmt.executeUpdate();
                        successCount++;
                    } catch (Exception ex) {
                        failCount++;
                        System.err.println("Failed to insert row: " + String.join(" | ", line));
                        ex.printStackTrace();
                    }
                }

                System.out.printf("Finished loading yield_amount table. %d rows inserted, %d failed.%n", successCount, failCount);

            } catch (Exception e) {
                System.err.println("Error reading or inserting yield_amount CSV.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error checking yield_amount table.");
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param s the date string
     * @return true if the string is in the valid date format, otherwise return false
     */
    private static boolean isValidDate(String s) {
        return s != null && s.matches("\\d{4}-\\d{2}-\\d{2}");
    }




}
