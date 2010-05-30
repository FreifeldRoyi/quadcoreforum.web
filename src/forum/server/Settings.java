package forum.server;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

import forum.server.domainlayer.SystemLogger;
import forum.server.updatedpersistentlayer.SessionFactoryUtil;

/**
 * @author Vitali Sepetnitsky
 *
 */

/**
 * This class contains static fields which define the general settings of the forum
 * application, like the database file name, logger name etc.
 */
public class Settings {

	public static final String LOG_FILE_NAME = "forumQuadCore.log";

	public static String DB_FILES_LOCATION = 
		"src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator");
	public static String DB_FILE_NAME = "QuadCoreForumDB";

	public static String SCHEMA_FILE_FULL_LOCATION = DB_FILES_LOCATION + DB_FILE_NAME + ".xsd";
	public static String DB_FILE_FULL_LOCATION 	= DB_FILES_LOCATION + DB_FILE_NAME + ".xml";

	//	public static final String DB_INITIAL_FILE = "src" + System.getProperty("file.separator") +
	//	"testing" + System.getProperty("file.separator") + "InitialDB.xml";

	public static final String DB_INITIAL_FILE = "src" + System.getProperty("file.separator") +
	"testing" + System.getProperty("file.separator") + "InitialDB.txt";

	private static Queue<String> mainScriptQuery;

	static {
		mainScriptQuery = new LinkedList<String>();
		String tMainScriptQuery = "";
		try {
			BufferedReader tInitialDBReader = new BufferedReader(new FileReader(new File(Settings.DB_INITIAL_FILE)));
			String tCurrentLine = "";
			while ((tCurrentLine = tInitialDBReader.readLine()) != null)
				tMainScriptQuery += tCurrentLine + "\n";
			tInitialDBReader.close();
		}
		catch (FileNotFoundException e) {
			SystemLogger.severe("Can't find initial script file");
		}
		catch (IOException e) {
			SystemLogger.severe("Can't load the database initialization script.");
		}

		while (true) {
			String firstQuery = tMainScriptQuery.substring(0, tMainScriptQuery.indexOf(";") + 1);
			mainScriptQuery.add(firstQuery);
			tMainScriptQuery = tMainScriptQuery.substring(tMainScriptQuery.indexOf(";") + 1);
			if (tMainScriptQuery.indexOf(";") == -1)
				break;
		}
	}

	private static boolean executeQuery(String databaseName, String query) {
		boolean toReturn = true;
		Connection connection = null;
		Statement statement = null;
		try {
			String url = "jdbc:mysql://localhost/" + databaseName;
			connection = DriverManager.getConnection(url, "root", "1234");
			statement = connection.createStatement();

			statement.executeUpdate(query);

		}
		catch (Exception e) {
			e.printStackTrace();
			SystemLogger.severe("Can't prepare Test Database");
			toReturn = false;
		}
		finally {
			if (statement != null) {
				try {
					statement.close();
				}
				catch (SQLException e) {
					toReturn = false;
				}
			}
			if (connection != null) {
				try {
					connection.close();
				}
				catch (SQLException e) {
					toReturn = false;
				}
			}
		}
		return toReturn;

	}

	/**
	 * This methods creates a new empty database file in order to allow the test methods to modify
	 * the database without changing it
	 *  
	 * @throws IOException
	 * 		In case an error occurred while trying writing to the database
	 */
	public static void switchToTestMode() throws IOException {	
		boolean toReturn = true;
		try {
			/* Load the hsqldb driver which implements the jdbc interface */
			Class.forName("com.mysql.jdbc.Driver" );
		} 
		catch (Exception e) {
			SystemLogger.severe("failed to load MYSQLDB JDBC driver and therefore can't run tests.");
			toReturn = false;
		}


		if (toReturn)
			toReturn = Settings.executeQuery("mysql", "CREATE DATABASE IF NOT EXISTS QuadCoreForumDBTest");

		Queue<String> temp = new LinkedList<String>();
		while (!mainScriptQuery.isEmpty()) {
			String tCurrentQuery = mainScriptQuery.remove();
			temp.add(tCurrentQuery);
			toReturn = Settings.executeQuery("QuadCoreForumDBTest", tCurrentQuery);
			if (!toReturn) break;
		}
		mainScriptQuery = temp;

		if (toReturn)
			SessionFactoryUtil.reconnectToOtherDatabase("QuadCoreForumDBTest");
		else
			SystemLogger.severe("The test can't proceed");
	}

	/**
	 * This methods switches back to a regular running mode, after a test database was created and used
	 * in order to test the forum operations
	 */
	public static void switchToRegularMode() {
		SessionFactoryUtil.reconnectToOtherDatabase("QuadCoreForumDB");
		Connection connection = null;
		Statement statement = null;
		try {
			String url = "jdbc:mysql://localhost/mysql";
			connection = DriverManager.getConnection(url, "root", "1234");
			statement = connection.createStatement();
			String tCreateTestDBQuery = "DROP DATABASE IF EXISTS QuadCoreForumDBTest";

			statement.executeUpdate(tCreateTestDBQuery);
		}
		catch (Exception e) {
			e.printStackTrace();
			SystemLogger.severe("Can't create Test Database");
		}
		finally {
			if (statement != null) {
				try {
					statement.close();
				}
				catch (SQLException e) {}
			}
			if (connection != null) {
				try {
					connection.close();
				}
				catch (SQLException e) {}
			}
		}
	}
}