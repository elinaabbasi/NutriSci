package controller;


/**
 * the interface represents a command that can be executed to perform a food swap
 * operation based on the user's goals. 
 * This interface follows the Command design pattern because it encapsulates a request
 * as an object which allows you to represent clients with different requests.
 */
public interface ISwapCommand {
	
	/**
     * the method execute executes the command.
     * @return a String showing the suggested swap for the user based on their goals.
     */
    String execute();
}