package view;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;


/**
 * an abstract factory for creating nutrient comparison charts using JFreeChart.
 * 
 * this class uses the Factory Method design pattern to allow different creations
 * of chart types
 * 
 * the subclasses must implement the createChart(String, String, DefaultCategoryDataset) method
 * to specify how a specific chart type should be created
 */
public abstract class CustomChartFactory {
   
	/**
     * abstract method so that the subclasses can create a JFreeChart based on their own implementation and information provided (nutrient, user, and data) 
     *
     * @param nutrient the nutrient being examined
     * @param userName the name of the user
     * @param dataset the dataset containing the nutrient values per day
     * @return a JFreeChart representing the nutrient comparison
     */
	public abstract JFreeChart createChart(String nutrient, String userName, DefaultCategoryDataset dataset);

	
	/**
     * Factory Method that returns a chart factory based on the chart type.
     *
     * @param chartType the chosen chart type
     * @return an instance of BarChartFactory or LineChartFactory
     */
    public static CustomChartFactory getFactory(String chartType) {
        if ("Bar Chart".equals(chartType)) {
            return new BarChartFactory();
        } else {
            return new LineChartFactory();
        }
    }
}


/**
 * Factory for creating bar charts that visualize nutrient data over time.
 */
class BarChartFactory extends CustomChartFactory {
	
	/**
     * creates a bar chart comparing nutrient values over time for a user
     *
     * @param nutrient the nutrient being examined
     * @param userName the name of the user
     * @param dataset the data to be visualized
     * @return a JFreeChart bar chart
     */
    @Override
    public JFreeChart createChart(String nutrient, String userName, DefaultCategoryDataset dataset) {
        return org.jfree.chart.ChartFactory.createBarChart(
                nutrient + " Swap Impact Comparison for " + userName,
                "Day",
                nutrient + " (per 100g)",
                dataset
        );
    }
}

/**
 * Factory for creating line charts that visualize nutrient data over time.
 */
class LineChartFactory extends CustomChartFactory {
	
	/**
     * creates a line chart comparing nutrient values over time for a user
     *
     * @param nutrient the nutrient being examined
     * @param userName the name of the user
     * @param dataset the data to be visualized
     * @return a JFreeChart line chart
     */
    @Override
    public JFreeChart createChart(String nutrient, String userName, DefaultCategoryDataset dataset) {
        return org.jfree.chart.ChartFactory.createLineChart(
                nutrient + " Swap Impact Comparison for " + userName,
                "Day",
                nutrient + " (per 100g)",
                dataset
        );
    }
}
