package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Set;


/**
 * this class generates a bar chart that compares the nutrient composition of an original food versus a suggested swap
 *
 * extends AbstractTemplateComparisonChart and overrides methods to define the specific nutrients
 * and create the chart using JFreeChart
 */

public class SwapComparisonChart extends AbstractTemplateComparisonChart {

	
	/**
	 * constructor for class SwapComparisonChart with the given attributes
	 *
	 * @param originalFoodId the food id of the original food item
	 * @param suggestedFoodId the food id of the suggested swap food item
	 */
    public SwapComparisonChart(int originalFoodId, int suggestedFoodId) {
        super(originalFoodId, suggestedFoodId);
    }

    /**
     *implement abstract method to return the main nutrients
     *
     * @return a set of nutrient names to include in the comparison
     */
    @Override
    protected Set<String> getMainNutrients() {
        return Set.of(
            "PROTEIN",
            "FAT (TOTAL LIPIDS)",
            "CARBOHYDRATE, TOTAL (BY DIFFERENCE)",
            "FIBRE, TOTAL DIETARY",
            "SUGARS, TOTAL",
            "SODIUM",
            "POTASSIUM",
            "ZINC",
            "ENERGY (KILOCALORIES)",
            "CALCIUM",
            "IRON"
        );
    }

    
    /**
     *  implement the abstract method to create the chart with the dataset
     * 	creates a bar chart for comparing the original and swapped nutrient values
     *	pink bar is for original, blue is for swapped
     *	chart contains tooltips that shows the amount of the nutrient in the hovered food,
     *	the difference compared to the other food and whether the nutrient amount increased or decreased
     *
     * @param dataset the dataset containing nutrient values for both foods
     * @param originalName the name of the original food item
     * @param swapName the name of the swapped food item
     */
    @Override
    protected void createChart(DefaultCategoryDataset dataset, String originalName, String swapName) {
        String chartTitle = "Nutrient Comparison: " + originalName + " vs " + swapName;

        JFreeChart chart = ChartFactory.createBarChart(
                chartTitle,
                "Nutrient",
                "Amount per 100g",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0)	//rotate nutrient labelling
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);

        BarRenderer renderer = new BarRenderer();
        DecimalFormat df = new DecimalFormat("#.##");

        // tooltips displaying the changes in nutrient values
        renderer.setDefaultToolTipGenerator(new CategoryToolTipGenerator() {
            @Override
            public String generateToolTip(CategoryDataset dataset, int row, int column) {
                String nutrient = (String) dataset.getColumnKey(column);
                String food = (String) dataset.getRowKey(row);
                double value = dataset.getValue(row, column).doubleValue();

                double original = originalNutrientMap.getOrDefault(nutrient, 0.0);
                double swap = swapNutrientMap.getOrDefault(nutrient, 0.0);

                double diff;
                String direction;
                String comparedTo;

                if (food.equals(originalName)) {
                    // hover over Original
                    diff = value - swap;
                    comparedTo = swapName;
                } else {
                    // hover over Swap
                    diff = value - original;
                    comparedTo = originalName;
                }

                if (diff > 0) {
                    direction = "increased";
                } else if (diff < 0) {
                    direction = "decreased";
                } else {
                    direction = "no change";
                }

                return String.format(
                    "%s in %s: %.2f g (%s by %.2f g compared to %s)",
                    nutrient,
                    food,
                    value,
                    direction,
                    Math.abs(diff),
                    comparedTo
                );
            }
        });





        plot.setRenderer(renderer);
        renderer.setSeriesPaint(0, new Color(255, 192, 203)); // Pink for original
        renderer.setSeriesPaint(1, new Color(100, 149, 237)); // Cornflower Blue for swapped
    }


}