package controller;

import database.DBConnection;



import model.UserProfile;

import java.sql.*;

//Use of AI: Used AI to debug code, create database queries, and improve operations
/**
 * implements the IProfileManager interface to provide functionality for
 * saving UserProfile objects to the database and loading the UserProfile objects from the database.
 * 
 * This class handles both saving and updating operations depending on whether the
 * profile already exists in the database.
 */
public class ProfileManager implements IProfileManager{

	
	/**
     * this method saves a users profile to the database.
     * if a profile with the given name already exists, it updates the record.
     * otherwise, it inserts a new row for that name.
     *
     * @param profile the profile to save which is of type UserProfile 
     */    public void saveProfile(UserProfile profile) {
        String checkQuery = "SELECT COUNT(*) FROM user_profile WHERE name = ?";
        String insertQuery = "INSERT INTO user_profile (name, sex, date_of_birth, height, weight, unit_system) VALUES (?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE user_profile SET sex = ?, date_of_birth = ?, height = ?, weight = ?, unit_system = ? WHERE name = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
        		) {
            // check if the profile exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, profile.getName());
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                boolean exists = rs.getInt(1) > 0;

                if (exists) {
                	//update the existing profile
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, profile.getSex());
                        updateStmt.setDate(2, Date.valueOf(profile.getDateOfBirth()));
                        updateStmt.setDouble(3, profile.getHeight());
                        updateStmt.setDouble(4, profile.getWeight());
                        updateStmt.setString(5, profile.getUnitSystem());
                        updateStmt.setString(6, profile.getName());

                        updateStmt.executeUpdate();
                        System.out.println("Profile updated: " + profile.getName());
                    }
                } else {
                	
                	//insert a new row (profile)
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, profile.getName());
                        insertStmt.setString(2, profile.getSex());
                        insertStmt.setDate(3, Date.valueOf(profile.getDateOfBirth()));
                        insertStmt.setDouble(4, profile.getHeight());
                        insertStmt.setDouble(5, profile.getWeight());
                        insertStmt.setString(6, profile.getUnitSystem());

                        insertStmt.executeUpdate();
                        System.out.println("Profile inserted: " + profile.getName());
                    }
                }
            }

            //catch exception if either conditional gives an error
        } catch (Exception e) {
            System.err.println("Error saving profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
     /**
      * loads a user profile from the database using their name
      *
      * @param name the name of the user
      * @return a UserProfile object if found, otherwise return null
      */    
     public static UserProfile loadProfileByName(String name) {
        String query = "SELECT * FROM user_profile WHERE name = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();

             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserProfile(
                        rs.getString("name"),
                        rs.getString("sex"),
                        rs.getDate("date_of_birth").toString(),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getString("unit_system")
                );
            }

        } catch (Exception e) {
            System.err.println("Error loading profile: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
