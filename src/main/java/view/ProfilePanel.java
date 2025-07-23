package view;

import controller.ProfileManager;
import model.UserProfile;
import utils.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

//Use of AI: Used AI to debug code and improve operations
/**
 * a gui panel that allows users to create, save, and load a UserProfile
 * uses the ProfileManager class to get the user profiles so that it can save and load the profiles
 * has input fields for user attributes including name, sex, date of birth, height, weight, and units
 * 
 */
public class ProfilePanel extends JPanel {

    private JTextField nameField, dobField, heightField, weightField;
    private JComboBox<String> sexBox, unitSystemBox;
    private JButton saveButton, loadButton;
    private UserProfile profile;
    private Consumer<UserProfile> onProfileLoaded;

    /**
     * sets a callback to be used when a user profile is successfully loaded.
     * @param callback the user that receives the loaded UserProfile
     */
    public void setOnProfileLoaded(Consumer<UserProfile> callback) {
        this.onProfileLoaded = callback;
    }

    /**
     * constructor for a new ProfilePanel class with inputs and buttons to
     * create, save, and load a user profile.
     * 
     * includes validation for input formats and error handling
     * when a profile is successfully loaded, it becomes the active context profile via AppContext,
     * and a callback is triggered via onProfileLoaded method    
     * */
    public ProfilePanel() {
        setLayout(new GridLayout(8, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("User Profile"));

        // UI Components
        nameField = new JTextField();
        sexBox = new JComboBox<>(new String[]{"Male", "Female"});
        dobField = new JTextField("YYYY-MM-DD");
        heightField = new JTextField();
        weightField = new JTextField();
        unitSystemBox = new JComboBox<>(new String[]{"Metric", "Imperial"});
        saveButton = new JButton("Save Profile");
        loadButton = new JButton("Load Profile");

        // layout
        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Sex:")); add(sexBox);
        add(new JLabel("Date of Birth:")); add(dobField);
        add(new JLabel("Height:")); add(heightField);
        add(new JLabel("Weight:")); add(weightField);
        add(new JLabel("Unit System:")); add(unitSystemBox);
        add(saveButton); add(loadButton);

        // save profile action listener logic
        saveButton.addActionListener((ActionEvent e) -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                
                String dobText = dobField.getText().trim();
                try {
                    java.sql.Date.valueOf(dobText); // validates format "YYYY-MM-DD"
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
                    return;
                }
                
                double height = Double.parseDouble(heightField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                if (height <= 0 || weight <= 0) throw new IllegalArgumentException("Height and Weight must be positive.");

                profile = new UserProfile(
                        name,
                        sexBox.getSelectedItem().toString(),
                        dobField.getText().trim(),
                        height,
                        weight,
                        unitSystemBox.getSelectedItem().toString()
                );

                ProfileManager profileManager = new ProfileManager();
				profileManager.saveProfile(profile);
                AppContext.setCurrentProfile(profile);

                JOptionPane.showMessageDialog(this, "Profile saved and active.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Height and Weight must be valid numbers.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // load action listener logic
        loadButton.addActionListener((ActionEvent e) -> {
            String nameToLoad = JOptionPane.showInputDialog(this, "Enter name to load profile:");
            if (nameToLoad == null || nameToLoad.trim().isEmpty()) return;

            UserProfile loaded = ProfileManager.loadProfileByName(nameToLoad.trim());
            if (loaded != null) {
                profile = loaded;
                AppContext.setCurrentProfile(profile);
                if (onProfileLoaded != null) {
                    onProfileLoaded.accept(profile);
                }

                nameField.setText(profile.getName());
                sexBox.setSelectedItem(profile.getSex());
                dobField.setText(profile.getDateOfBirth());
                heightField.setText(String.valueOf(profile.getHeight()));
                weightField.setText(String.valueOf(profile.getWeight()));
                unitSystemBox.setSelectedItem(profile.getUnitSystem());

                JOptionPane.showMessageDialog(this, " Loaded: " + profile.getName());
            } else {
                JOptionPane.showMessageDialog(this, " Profile not found.");
            }
        });
    }

    /**
     * @return the currently active UserProfile
     */
    public UserProfile getProfile() {
        return profile;
    }
}
