import javax.swing.*;
import view.ProfilePanel;
import view.MealLoggerPanel;
import view.SwapPanel;
import view.NutrientTrendPanel;
import view.CFGPlatePanel;
import view.SwapImpactPanel;
import view.SwapApplierPanel;
import view.GoalOptimizerPanel;
import database.CNFLoader;

public class Main {
    public static void main(String[] args) {

        // load CSV files only if tables are empty
        CNFLoader.loadFoodGroups("data/FOOD GROUP.csv");
        CNFLoader.loadFoodSources("data/FOOD SOURCE.csv");
        CNFLoader.loadFoodItems("data/FOOD NAME.csv");

        CNFLoader.loadNutrientSources("data/NUTRIENT SOURCE.csv");
        CNFLoader.loadNutrients("data/NUTRIENT NAME.csv");
        CNFLoader.loadNutrientAmounts("data/NUTRIENT AMOUNT.csv");

        CNFLoader.loadMeasures("data/MEASURE NAME.csv");
        CNFLoader.loadConversionFactors("data/CONVERSION FACTOR.csv");

        CNFLoader.loadRefuseNames("data/REFUSE NAME.csv");
        CNFLoader.loadRefuseAmounts("data/REFUSE AMOUNT.csv");

        CNFLoader.loadYieldNames("data/YIELD NAME.csv");
        CNFLoader.loadYieldAmounts("data/YIELD AMOUNT.csv");

        // GUI launch
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("NutriSci - SwEATch to better!");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabbedPane = new JTabbedPane();
            ProfilePanel profilePanel = new ProfilePanel();
            tabbedPane.addTab("Profile Setup", profilePanel);
            frame.setContentPane(tabbedPane);
            frame.setVisible(true);

            profilePanel.setOnProfileLoaded(profile -> {
                tabbedPane.addTab("Meal Logger", new MealLoggerPanel(profile));
                tabbedPane.addTab("Food Swap", new SwapPanel(profile));
                tabbedPane.addTab("Nutrient Trend", new NutrientTrendPanel(profile));
                tabbedPane.addTab("Goal Optimizer", new GoalOptimizerPanel(profile));
                tabbedPane.addTab("CFG Plate", new CFGPlatePanel(profile));
                tabbedPane.addTab("Swap Impact", new SwapImpactPanel(profile));
                tabbedPane.addTab("Apply Swaps", new SwapApplierPanel(profile));
                tabbedPane.setSelectedIndex(1);
            });
        });
    }
}
