package view;

import database.DBConnection;
import java.util.Set;

import model.UserProfile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * * a gui panel that allows users to input the nutrient and time period so that they can view their nutrient intake over time
 *
 */
public class NutrientTrendPanel extends JPanel {
	
	//map user friendly names to database names
	private static final Map<String, String> nutrientMap = Map.of(
				"Energy", "ENERGY (KILOCALORIES)",
			    "Protein", "PROTEIN",
			    "Fat", "FAT (TOTAL LIPIDS)",
			    "Carbohydrate", "CARBOHYDRATE, TOTAL (BY DIFFERENCE)",
			    "Fiber", "FIBRE, TOTAL DIETARY",
			    "Sugar", "SUGARS, TOTAL"
		);
	
	//map the recommended daily nutrient portions based on the CFG
	private static final Map<String, Double> RECOMMENDED_DAILY = Map.of(
		    "ENERGY (KILOCALORIES)", 2500.0,
		    "PROTEIN", 50.0,
		    "FAT (TOTAL LIPIDS)", 60.0,
		    "CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 130.0,
		    "FIBRE, TOTAL DIETARY", 30.0,
		    "SUGARS, TOTAL", 50.0
		);


	//declare variables
    private final UserProfile user;

    private JComboBox<String> nutrientBox;
    private JButton loadButton;
    private JPanel chartContainer;
    
    private JButton pieChartButton;
    
    private JTextField startDateField;
    private JTextField endDateField;
    
    private Map<String, Double> currentTotals;
    
    private NutrientPieChart pieChartWindow = null;



	/**
	 * constructor for the NutrientTrendPanel given the userProfile attributes
	 * @param userProfile the user who wants to view their nutrient intake over time
	 */
    public NutrientTrendPanel(UserProfile userProfile) {
        this.user = userProfile;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("📊 Nutrient Intake Over Time"));

        // controls
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        nutrientBox = new JComboBox<>(new String[]{"Energy", "Protein", "Fat", "Carbohydrate", "Fiber", "Sugar"});
        loadButton = new JButton("Load Chart");
        
        pieChartButton = new JButton("Average Daily Portions");
        pieChartButton.setPreferredSize(new Dimension(500, 30));
        
        pieChartButton.setEnabled(true); // initially disabled

        startDateField = new JTextField(10);
        endDateField = new JTextField(10);

        topPanel.setLayout(new GridLayout(2, 4, 10, 10)); 

        topPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        topPanel.add(startDateField);
        topPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        topPanel.add(endDateField);

        topPanel.add(new JLabel("Select Nutrient:"));
        topPanel.add(nutrientBox);
        topPanel.add(loadButton);
        topPanel.add(pieChartButton);
        
        add(topPanel, BorderLayout.NORTH);

        // chart area
        chartContainer = new JPanel(new BorderLayout());
        add(chartContainer, BorderLayout.CENTER);

        // button action listener
        loadButton.addActionListener(e -> loadChart());
        pieChartButton.addActionListener(e -> loadAllNutrientAverages());

        
        add(loadButton, BorderLayout.SOUTH);
   
    }
    
    
    /**
     * display the pie chart to the user for their average daily intake
     */
    private void showPieChart() {
        if (currentTotals == null || currentTotals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to show.");
            return;
        }

        new NutrientPieChart(currentTotals, nutrientBox.getSelectedItem() + " Distribution");
    }


    /**
     * loads and displays a line chart showing the user's daily intake of the selected nutrient
     * over a certain time period
     * it checks the database for logged nutrient data, builds a dataset and creates the chart
     * if no data is found, a message is shown
     */
    private void loadChart() {
        String nutrient = (String) nutrientBox.getSelectedItem();
        String dbNutrient = nutrientMap.get(nutrient);
        if (dbNutrient == null) {
            JOptionPane.showMessageDialog(this, "Unknown nutrient.");
            return;
        }
        
        if (nutrient == null) {
            JOptionPane.showMessageDialog(this, "Select a nutrient.");
            return;
        }

        Map<String, Double> dailyTotals = new LinkedHashMap<>();

        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        if (!isValidDateRange(startDate, endDate)) {
            return;
        }
        
        String sql = """
        	    SELECT 
        	        lm.meal_date,
        	        SUM(na.value) AS total_amount
        	    FROM 
        	        logged_meal lm
        	    JOIN meal_ingredient mi ON lm.id = mi.meal_id
        	    JOIN nutrient_amount na ON mi.food_id = na.food_id
        	    JOIN nutrient n ON na.nutrient_id = n.id
        	    WHERE 
        	        n.name = ? AND lm.user_name = ? AND lm.meal_date BETWEEN ? AND ?
        	    GROUP BY lm.meal_date
        	    ORDER BY lm.meal_date;
        	""";

//SUM(na.value * mi.quantity_grams / 100)
        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //stmt.setString(1, nutrient);
        	stmt.setString(1, dbNutrient);
        	stmt.setString(2, user.getName());
        	stmt.setString(3, startDate);
        	stmt.setString(4, endDate);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getDate("meal_date").toString();
                double amount = rs.getDouble("total_amount");
                dailyTotals.put(date, amount);
            }
            
            currentTotals = dailyTotals;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data.");
            return;
        }

        if (dailyTotals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available for this nutrient.");
            return;
        }

        // plot chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : dailyTotals.entrySet()) {
            dataset.addValue(entry.getValue(), nutrient, entry.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                nutrient + " Intake Over Time for " + user.getName(),
                "Date",
                "Amount per 100g",
                dataset
        );

        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartContainer.revalidate();
    }
    
    
    /**
     * loads the user's average daily intake for all nutrients over a certain time period,
     * and displays a pie chart showing the nutrient distribution
     *
     * also shows a progress notification comparing intake to recommended daily values
     *
     * the results are grouped together by nutrient and meal date, then averaged over the number of days the
     * data is available for each nutrient
     */    
    private void loadAllNutrientAverages() {
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        if (!isValidDateRange(startDate, endDate)) {
            return;
        }

        //                SUM(na.value * mi.quantity_grams / 100) AS total

        String sql = """
            SELECT 
                n.name,
                lm.meal_date,
                SUM(na.value) AS total
            FROM 
                logged_meal lm
            JOIN meal_ingredient mi ON lm.id = mi.meal_id
            JOIN nutrient_amount na ON mi.food_id = na.food_id
            JOIN nutrient n ON na.nutrient_id = n.id
            WHERE 
                lm.user_name = ? AND lm.meal_date BETWEEN ? AND ?
            GROUP BY n.name, lm.meal_date;
        """;

        Map<String, Double> totals = new LinkedHashMap<>();
        Map<String, Set<String>> nutrientDateSet = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

        	System.out.println("Loading data for user " + user.getName() + " from " + startDate + " to " + endDate);

            stmt.setString(1, user.getName());
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nutrient = rs.getString("name").trim().toUpperCase();
                String date = rs.getDate("meal_date").toString();
                double value = rs.getDouble("total");

                totals.putIfAbsent(nutrient, 0.0);
                totals.put(nutrient, totals.get(nutrient) + value);

                nutrientDateSet.putIfAbsent(nutrient, new HashSet<>());
                nutrientDateSet.get(nutrient).add(date);
            }

            Map<String, Double> averages = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : totals.entrySet()) {
                String nutrient = entry.getKey();
                double total = entry.getValue();
                int days = nutrientDateSet.get(nutrient).size();
                if (days > 0) {
                    averages.put(nutrient, total / days);
                }
            }
            System.out.println("Averages: " + averages);


            String chartTitle = "Average Daily Nutrient Distribution (" + startDate + " to " + endDate + ")";

            if (pieChartWindow == null || !pieChartWindow.isVisible()) {
                pieChartWindow = new NutrientPieChart(averages, chartTitle);
            } else {
                pieChartWindow.updateData(averages, chartTitle);
            }

            pieChartWindow.setVisible(true);
            
            //can change if you dont want notification to show
            showProgressNotification(averages);


        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading nutrient data.");
        }
    }
    
    /**
     * shows user the progress notification message as a percentage
     * remove if you want no message to pop up
     * @param averages a map of nutrient names to average values
     */
    private void showProgressNotification(Map<String, Double> averages) {
        StringBuilder message = new StringBuilder("Your daily intake compared to Canada Food Guide:\n\n");

        for (Map.Entry<String, Double> entry : averages.entrySet()) {
            String nutrient = entry.getKey();
            double avg = entry.getValue();

            if (RECOMMENDED_DAILY.containsKey(nutrient)) {
                double recommended = RECOMMENDED_DAILY.get(nutrient);
                double percentage = (avg / recommended) * 100.0;

                message.append(String.format(
                    "%s: %.1f%% of recommended (%.1f / %.1f)\n",
                    nutrient, percentage, avg, recommended
                ));
            }
        }

        JOptionPane.showMessageDialog(this, message.toString(), "Daily Portion Progress", JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * checks whether the start and end dates are in the correct format and that
     * the start date is not after the end date
     *
     * @param startDateStr the start date in YYYY-MM-DD format
     * @param endDateStr the end date in YYYY-MM-DD format
     * @return true if the date range is valid. Otherwise, false
     */
    private boolean isValidDateRange(String startDateStr, String endDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            if (startDate.isAfter(endDate)) {
                JOptionPane.showMessageDialog(this, "Start date cannot be after end date.");
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter dates in YYYY-MM-DD format.");
            return false;
        }
    }

   
}
