package utils;

import model.UserProfile;




//this utility class manages the currently active UserProfile
public class AppContext {
	
	//define variable to hold the currently active user profile
    private static UserProfile currentProfile;

    
   
    /**
     * the method getCurrentProfile returns the currently active user profile.
     * @return the currently active UserProfile
     */
    public static UserProfile getCurrentProfile() {
        return currentProfile;
    }

    
    /**
     * the setCurrentProfile sets the currently active user profile.
     * @param profile the UserProfile to set
     */
    public static void setCurrentProfile(UserProfile profile) {
        currentProfile = profile;
    }

    /**
     * the isProfileSet method checks whether a user profile is currently set.
     * @return true if a profile is set, false otherwise
     */
    public static boolean isProfileSet() {
        return currentProfile != null;
    }
}
