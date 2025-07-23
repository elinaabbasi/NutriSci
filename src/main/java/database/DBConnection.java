package database;

import java.sql.Connection;



import java.sql.DriverManager;
import java.sql.SQLException;

//Use of AI: Used AI to figure out how to create a database connection

/*
 * this class uses the Singleton design pattern
 * this class ensures that only one instance of the database connection exists at any time
 */

public class DBConnection {
	
	//singleton instance of DBConnection
    private static DBConnection instance;
    
    //shared JDBC Connection object
    private static Connection connection;
    
    //database information
    private static final String URL = "jdbc:mysql://localhost:3306/nutrientDB";
    private static final String USER = "root";
    private static final String PASSWORD = "pass1234";

    /**
     * private constructor to prevent instantiation from outside the class
     * initializes the JDBC connection to the database
     * @throws SQLException if the conncection to the database fails
     */
    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * returns the singleton instance of DBConnection.
     * if the instance doesn't exist or the current connection is closed, a new instance is created.
     * @return the singleton DBConnection instance
     * @throws SQLException if access to database throws an error
     */
    public static DBConnection getInstance() throws SQLException {
        if (instance == null || connection.isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * 
     * @return the active connection object
     */
    public Connection getConnection() {
        return connection;
    }
}

