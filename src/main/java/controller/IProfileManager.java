package controller;

import model.UserProfile;


//interface for the ProfileManager class
public interface IProfileManager {

	
	/**
	 * define saveProfile method to save the users profile
	 * @param profile to save the users profile
	 */
	void saveProfile(UserProfile profile);
	
	
}
