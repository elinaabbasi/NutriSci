package view;

import controller.OptimizeGoalsCommand;
import controller.ISwapCommand;
import model.SwapGoal;
import model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Use of AI: Used AI to debug code and improve operations
/**
 * a gui panel that allows users to input goals (nutrient increase or decrease)
 * and receive food swap suggestions based on those goals
 *
 * this class uses the Command design pattern to pass the goal processing request
 * to the ISwapCommand interface
 */
public class GoalOptimizerPanel extends JPanel {

	//user profile
    private final UserProfile user;

    private JComboBox<String> nutrientBox1, nutrientBox2, dirBox1, dirBox2;
    private JTextField amountField1, amountField2;
    private JButton suggestButton;
    private JTextArea resultArea;

    private JComboBox<String> intensityBox1, intensityBox2;

    
    /**
     * constructor for a new GoalOptimizerPanel object for a given user profile.
     *
     * @param userProfile the profile of the user whose nutritional goals will be improved
     */
    public GoalOptimizerPanel(UserProfile userProfile) {
        this.user = userProfile;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(" Optimize Diet by Goals"));

        JPanel goalPanel = new JPanel(new GridLayout(3, 4, 10, 10));

        nutrientBox1 = new JComboBox<>(nutrients());
        dirBox1 = new JComboBox<>(new String[]{"increase", "decrease"});
        amountField1 = new JTextField();

        nutrientBox2 = new JComboBox<>(nutrients());
        dirBox2 = new JComboBox<>(new String[]{"increase", "decrease"});
        amountField2 = new JTextField();

        //layout setup for goals and intensities
        goalPanel.add(new JLabel("Nutrient Goal 1:"));
        goalPanel.add(nutrientBox1);
        goalPanel.add(dirBox1);
        goalPanel.add(amountField1);

        intensityBox1 = new JComboBox<>(intensityOptions());
        intensityBox2 = new JComboBox<>(intensityOptions());

        goalPanel.add(new JLabel("Intensity 1:"));
        goalPanel.add(intensityBox1);
        goalPanel.add(new JLabel("Intensity 2:"));
        goalPanel.add(intensityBox2);

        goalPanel.add(new JLabel("Nutrient Goal 2 (optional):"));
        goalPanel.add(nutrientBox2);
        goalPanel.add(dirBox2);
        goalPanel.add(amountField2);

        add(goalPanel, BorderLayout.NORTH);

        suggestButton = new JButton("Suggest Swaps");
        add(suggestButton, BorderLayout.SOUTH);

        resultArea = new JTextArea(12, 40);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        suggestButton.addActionListener(e -> suggestSwaps());

        //enable the amount fields only if "Custom" is selected
        intensityBox1.addActionListener(e -> amountField1.setEnabled("Custom".equals(intensityBox1.getSelectedItem())));
        intensityBox2.addActionListener(e -> amountField2.setEnabled("Custom".equals(intensityBox2.getSelectedItem())));
    }
    
    
    
    /**
     * gets input from the UI, creates SwapGoal instances,
     * and executes a command to suggest food swaps.
     *
     * handles the validation and displays the resulting suggestion, or an error message
     */
    private void suggestSwaps() {
        resultArea.setText("");
        List<SwapGoal> goals = new ArrayList<>();

        try {
            String n1 = (String) nutrientBox1.getSelectedItem();
            String d1 = (String) dirBox1.getSelectedItem();
            String i1 = (String) intensityBox1.getSelectedItem();
            double a1 = interpretIntensity(i1, amountField1.getText().trim());
            goals.add(new SwapGoal(n1, d1, a1));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "First goal is required and must be valid.");
            return;
        }

        try {
            String n2 = (String) nutrientBox2.getSelectedItem();
            String d2 = (String) dirBox2.getSelectedItem();
            String i2 = (String) intensityBox2.getSelectedItem();
            String amount2 = amountField2.getText().trim();
            if (!"None".equals(i2) && (!amount2.isEmpty() || !"Custom".equals(i2))) {
                double a2 = interpretIntensity(i2, amount2);
                goals.add(new SwapGoal(n2, d2, a2));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Second goal is optional, but must be valid if filled.");
            return;
        }

        // execute command
        ISwapCommand command = new OptimizeGoalsCommand(goals, user);
        String suggestion = command.execute();
        resultArea.setText(suggestion != null ? suggestion : " No swap found that satisfies all goals.");
    }

    /**
     * sets the intensity selection to be a specific value
     *
     * @param label the chosen intensity label
     * @param textFieldValue the value entered by the user (if "Custom")
     * @return a numeric intensity value
     * @throws NumberFormatException if custom input is invalid
     */
    private double interpretIntensity(String label, String textFieldValue) throws NumberFormatException {
        switch (label) {
            case "Custom": return Double.parseDouble(textFieldValue);
            case "Slightly": return 1.0;
            case "Moderately": return 2.0;
            case "Significantly": return 3.5;
            default: throw new IllegalArgumentException("Unknown intensity");
        }
    }

    /**
     * provides a list of nutrients that users can select for goal setting
     *
     * @return an array of nutrient names
     */
    private String[] nutrients() {
        return new String[]{"Energy", "Protein", "Fat", "Carbohydrate", "Fiber", "Sugar"};
    }

    /**
     * provides a list of available intensity options for nutrient goals
     *
     * @return an array of intensity labels
     */
    private String[] intensityOptions() {
        return new String[]{"Custom", "Slightly", "Moderately", "Significantly"};
    }
}
