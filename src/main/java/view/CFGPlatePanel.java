package view;

import database.DBConnection;

import model.UserProfile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import utils.CFGClassifier;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

//Use of AI: Used AI to debug code and improve operations
/**
 * a panel that visualizes a user's meal composition according to Canada's Food Guide.
 * 
 * this class allows the user to load a pie chart showing the distribution of their
 * logged food intake across Canada's Food Guide (CFG) categories, and also opens a 
 * bar chart window that compares their intake to CFG recommendations
 */
public class CFGPlatePanel extends JPanel {

    private final UserProfile user;		//users data being visualized
    private JButton loadButton;		//button to trigger the plate visualization
    private JPanel chartContainer;		//hold and display the generated chart

    /**
     * constructor for class CFGPlatePanel for the given user profile.
     *
     * @param userProfile the user whose plate data will be visualized
     */
    public CFGPlatePanel(UserProfile userProfile) {
        this.user = userProfile;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(" Canada Food Guide Plate"));

        // user name label
        JLabel label = new JLabel("Current User: " + user.getName());
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label, BorderLayout.NORTH);

        // chart container
        chartContainer = new JPanel(new BorderLayout());
        add(chartContainer, BorderLayout.CENTER);

        // load button
        loadButton = new JButton("Compare Plate");
        add(loadButton, BorderLayout.SOUTH);

        //action listener to load chart
        loadButton.addActionListener(e -> loadPlateChart());
    }
    
    /**
     * loads and displays the user's plate data as a pie chart.
     * 
     * this method gets the total grams of food eaten by the CFG food group from the database.
     * seperates each food group into a CFG category
     * displays a pie chart showing the users actual plate porportions
     * also displays a bar chart to compare against the ideal CFG porportions
     */
    private void loadPlateChart() {
        Map<String, Double> gramsByCFG = new HashMap<>();

        //sql query
        String sql = """
            SELECT fg.id, SUM(mi.quantity_grams) AS total_grams
            FROM logged_meal lm
            JOIN meal_ingredient mi ON lm.id = mi.meal_id
            JOIN food_name fn ON mi.food_id = fn.id
            JOIN food_group fg ON fn.food_group_id = fg.id
            WHERE lm.user_name = ?
            GROUP BY fg.id;
        """;

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            ResultSet rs = stmt.executeQuery();

            //group and seperate the grams by the CFG category
            while (rs.next()) {
                int groupId = rs.getInt("id");
                double grams = rs.getDouble("total_grams");

                String cfgGroup = CFGClassifier.classify(groupId);
                gramsByCFG.put(cfgGroup, gramsByCFG.getOrDefault(cfgGroup, 0.0) + grams);
            }
            
            //handle erros if anything goes wrong
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading plate data.");
            return;
        }

        // add data to the dataset for the pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : gramsByCFG.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        //create and display the pie chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Your Plate Composition",
                dataset,
                true, true, false
        );

        chartContainer.removeAll();
        chartContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartContainer.revalidate();

        // also show bar chart comparison to CFG
        new CFGComparisonChart(user).setVisible(true);
    }
}
