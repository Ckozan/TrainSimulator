package simulator375.pkg2.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL {

	public static Connection connection;
	private static Statement statement;
	private static String query;
        
        static {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cis_375_schema", "root", "");
            } catch (SQLException ex) {
                Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
            }
		
        }

	/**
	 * Creates an instance of the database connection. 
	 *
	 * @param  databaseName	The name of the database you wish to connect to
	 * @param  username	Username of the database, normally just 'root'
	 * @param  password Password of the database, normally just ""
	 */
	public MySQL(String databaseName, String username, String password) {

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Selects rows from the database based on the passed in condition.
	 *
	 * @param  tableName	The table to which you'd like to select data from
	 * @param  condition	Which condition needs to be met to select that row
	 * @param  columns Which elements to select. Leave this parameter blank if you want to select all elements
	 */
	public static ResultSet Select(String tableName, String condition, String... columns) throws SQLException {
		if (columns.length == 0) {
			query = "SELECT * FROM `" + tableName + "` WHERE " + condition;
		} else {
			query = "SELECT ";
                        int counter = 0;
			for (String col : columns){
                     if (counter == columns.length-1) {
				query += col + " ";
			} else
				query += col + ",";
			counter++;
                        }
			query += "FROM `" + tableName + "` WHERE " + condition;
		}
		System.out.println(query);

		statement = connection.createStatement();
		return statement.executeQuery(query);

	}

	/**
	 * Inserts rows from the database
	 *
	 * @param  tableName	The table to which you'd like to insert data to
	 * @param  entries A key value pair of entries where the key is the database variable, and the value is the value.
	 * @see HashMap
	 */
	public static boolean Insert(String tableName, HashMap<String, String> entries) throws SQLException {
		query = "INSERT INTO `" + tableName + "` (";
		int counter = 0;
		for (String key : entries.keySet()) {
			if (counter == entries.keySet().size() - 1) {
				query += key + "";
			} else
				query += key + ",";
			counter++;
		}
		counter = 0;
		query += ") VALUES (";
		for (String key : entries.keySet()) {
			if (counter == entries.keySet().size() - 1) {
				query += "'" + entries.get(key) + "'" + "";
			} else
				query += "'" + entries.get(key) + "'" + ",";
			counter++;
		}
		query += ")";
		return connection.createStatement().execute(query);
	}

	/**
	 * Updates rows from the database based on the passed in condition.
	 *
	 * @param  tableName	The table to which you'd like to update data
	 * @param  condition	Which condition needs to be met to update that row
	 * @param  entries A key value pair of entries where the key is the database variable, and the value is the value.
	 * @see HashMap
	 */
	public static boolean Update(String tableName, String condition, HashMap<String, String> entries) throws SQLException {
		query = "UPDATE `" + tableName + "` SET ";
		int counter = 0;
		for (String key : entries.keySet()) {
			if (counter == entries.keySet().size() - 1)
				query += key + " = '" + entries.get(key) + "'";
			else
				query += key + " = '" + entries.get(key) + "', ";
		}
		query += " WHERE " + condition;
		System.out.println(query);

		statement = connection.createStatement();
		return statement.execute(query);

	}
	
	/**
	 * Deletes rows from the database based on the passed in condition.
	 *
	 * @param  tableName	The table to which you'd like to delete data from
	 * @param  condition	Which condition needs to be met to delete that row
	 */
	public static boolean Delete(String tableName, String condition) throws SQLException {
		query = "DELETE FROM `" + tableName + "` WHERE " + condition;
		System.out.println(query);
		return connection.createStatement().execute(query);
	}

}
