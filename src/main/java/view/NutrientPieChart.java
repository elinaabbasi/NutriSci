package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Use of AI: Used AI to debug code and improve operations
/**
 * the NutrientPieChart class displays a pie chart visualization of the nutrient breakdown for a meal
 */
public class NutrientPieChart extends JFrame {
	
	private DefaultPieDataset dataset;
    private JFreeChart chart;

    /**
     * a map from nutrient database names to more user friendly names
     */
	private static final Map<String, String> DISPLAY_NAME_MAP = Map.ofEntries(
		    Map.entry("ENERGY (KILOCALORIES)", "Energy"),
		    Map.entry("PROTEIN", "Protein"),
		    Map.entry("FAT (TOTAL LIPIDS)", "Fat"),
		    Map.entry("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", "Carbohydrate"),
		    Map.entry("FIBRE, TOTAL DIETARY", "Fiber"),
		    Map.entry("SUGARS, TOTAL", "Sugar"),
		    Map.entry("CALCIUM", "Calcium"),
		    Map.entry("IRON", "Iron"),
		    Map.entry("SODIUM", "Sodium"),
		    Map.entry("POTASSIUM", "Potassium")
		);


	/**
     * constructor for class NutrientPieChart given its attributes
     *
     * @param nutrients the map of nutrient names to their values
     * @param chartTitle the title to display on the chart
     */
	public NutrientPieChart(Map<String, Double> nutrients, String chartTitle) {
        setTitle("Nutrient Distribution Pie Chart");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.dataset = createDataset(nutrients);
        this.chart = createChart(dataset, chartTitle);

        setContentPane(new ChartPanel(chart));
        setVisible(true);
    }
	
	
	/**
     * creates the dataset for the nutrient pie chart
     * the top 10 nutrients are shown individually
     * the rest are grouped into "Other"
     *
     * @param nutrients the map of nutrient names and their values
     * @return a DefaultPieDataset with nutrient categories
     */
	private DefaultPieDataset createDataset(Map<String, Double> nutrients) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        double other = 0;

        for (Map.Entry<String, Double> entry : nutrients.entrySet()) {
            String dbName = entry.getKey().toUpperCase().trim();
            double value = entry.getValue();

            if (DISPLAY_NAME_MAP.containsKey(dbName)) {
                String label = DISPLAY_NAME_MAP.get(dbName);
                dataset.setValue(label, value);
            } else {
                other += value;
            }
        }

        if (other > 0) {
            dataset.setValue("Other", other);
        }

        return dataset;
    }
	
	
	/**
     * creates a pie chart from the dataset with customized labelling
     *
     * @param dataset the nutrient dataset
     * @param title the chart title
     * @return a JFreeChart pie chart
     */
	private JFreeChart createChart(DefaultPieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart(
            title,
            dataset,
          //legend  tooltips   URLs
            true, true, false
            
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
            "{0}: {1}g ({2})",
            new DecimalFormat("0.00"),
            new DecimalFormat("0.0%")
        ));

        return chart;
    }
	
	
	/**
     * updates the pie chart with the new nutrient data and title
     * this can be used to reuse the same window for different meals
     *
     * @param nutrients the new nutrient data to display
     * @param newTitle the updated chart title
     */
	public void updateData(Map<String, Double> nutrients, String newTitle) {
        this.dataset.clear();
        double other = 0;

        for (Map.Entry<String, Double> entry : nutrients.entrySet()) {
            String dbName = entry.getKey().toUpperCase().trim();
            double value = entry.getValue();

            if (DISPLAY_NAME_MAP.containsKey(dbName)) {
                String label = DISPLAY_NAME_MAP.get(dbName);
                dataset.setValue(label, value);
            } else {
                other += value;
            }
        }

        if (other > 0) {
            dataset.setValue("Other", other);
        }

        chart.setTitle(newTitle);
        repaint();
    }
	
}
