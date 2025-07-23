package controller;

import model.SwapGoal;
import model.UserProfile;
import java.util.List;

/**
 * follows the command design pattern
 * this is a concrete implementation of the ISwapCommand interface that
 * optimizes a list of SwapGoal objects for a specific user.
 * 
 * When the method execute() is called, it sends the request to {@link SwapEngine#optimizeGoals(List, UserProfile)}.
 */
public class OptimizeGoalsCommand implements ISwapCommand {
	
	//declare variables
	
	//the list of swap goals to optimize
    private final List<SwapGoal> goals;
    
    //the user that is associated with the goals to optimize
    private final UserProfile user;

    
    /**
     * Constructor for OptimizeGoalsCommand with the specified goals and user.
     *
     * @param goals which is the list of SwapGoal instances to be improved
     * @param user the user whose goals are to be improved which is of type UserProfile
     */
    public OptimizeGoalsCommand(List<SwapGoal> goals, UserProfile user) {
        this.goals = goals;
        this.user = user;
    }

    /**
     * executes the command by invoking the goal optimization logic in SwapEngine
     *
     * @return a String message indicating the outcome of the goal optimization
     */
    @Override
    public String execute() {
        return SwapEngine.optimizeGoals(goals, user);
    }
}