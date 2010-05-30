/**
 * This class implements a logger using the Singleton pattern.
 * 
 * The class has one public static method called logAMessage which allows the user to log a message with a
 * level he wants.
 * 
 * The logged messages are formatted and logged to our QuadCore log file.
 */
package forum.server.domainlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.logging.ConsoleHandler;

import java.text.*;
import java.io.*;

import java.util.logging.*;

import forum.server.Settings;



public class SystemLogger {

	private static Logger LOGGER; // The name of the log file which saves the log messages
	private static Formatter CUSTOM_FORMATTER; // The formatter of the log messages

	private static final boolean APPEND_NEW_MESSAGES_TO_OLD = true;
	private static final boolean LOG_TO_FILE_BY_DEFAULT = false ;
	private static final boolean LOG_TO_CONSOLE_BY_DEFAULT = true ;
	private static final String MESSAGE_DEFAULT_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss";
	private static final Level DEFAULT_LOG_LEVEL = Level.ALL ;

	/**
	 * Creates and initializes the system logger
	 */
	static {
		createLogger();
	}

	/**
	 * Enables the logging to the console.
	 */
	private static void setConsoleLogging() {
		final ConsoleHandler console = new ConsoleHandler() ;
		console.setFilter(new Filter() {
			public boolean isLoggable(LogRecord record) {
				return record.getLoggerName().equals("quadcoreforum");
			}
		});
		console.setFormatter(SystemLogger.getCustomFormatter()) ;
		console.setLevel(SystemLogger.DEFAULT_LOG_LEVEL) ;
		SystemLogger.LOGGER.addHandler(console) ;
	}

	/**
	 * Adds the system logger the ability to log the messages to a file
	 *  
	 * @throws IOException
	 * 		If there is IO problems while openning the file
	 */
	private static void setFileLogging() throws IOException {
		final FileHandler tOutputFile = new FileHandler(Settings.LOG_FILE_NAME,
				SystemLogger.APPEND_NEW_MESSAGES_TO_OLD) ;
		tOutputFile.setFormatter(SystemLogger.getCustomFormatter()) ;
		tOutputFile.setLevel(SystemLogger.DEFAULT_LOG_LEVEL) ;
		SystemLogger.LOGGER.addHandler(tOutputFile);
	}

	/**
	 * Disables the logging to the console
	 */
	private static void disableConsoleLogging() {
		final Collection<Handler> handlers = Arrays.asList(Logger.getLogger("").getHandlers());
		for (final Handler handler: handlers)
			Logger.getLogger("").removeHandler(handler);
	}

	/**
	 * Creates a new formatter which records the message level and its subject
	 * 
	 * @return
	 * 		The created formatter
	 */
	private static Formatter getCustomFormatter() {
		if (SystemLogger.CUSTOM_FORMATTER == null)
			SystemLogger.CUSTOM_FORMATTER =  new Formatter() {
			public String format (LogRecord record) {
				return formatAGivenTime(System.currentTimeMillis()) + "\n" +
				record.getLevel() + " " + record.getMessage() + "\n\n";
			}
		};
		return SystemLogger.CUSTOM_FORMATTER;
	}


	/**
	 * 
	 * @return
	 * 		True if the logger creation succeeded and False otherwise 
	 */
	private static boolean createLogger() {
		boolean ans = true;
				try {
			SystemLogger.LOGGER = Logger.getLogger("quadcoreforum");
			// Remove all the default handlers
			for (Handler tHandler : SystemLogger.LOGGER.getHandlers())
				SystemLogger.LOGGER.removeHandler(tHandler);
			SystemLogger.LOGGER.setUseParentHandlers(false);
			//Set the log level specifying which message levels will be logged by this logger
			SystemLogger.LOGGER.setLevel(SystemLogger.DEFAULT_LOG_LEVEL);
			if (SystemLogger.LOG_TO_FILE_BY_DEFAULT)
				SystemLogger.setFileLogging();
			if (SystemLogger.LOG_TO_CONSOLE_BY_DEFAULT)
				SystemLogger.setConsoleLogging();
		}
		catch(IOException e) {
			ans = false;
		}
		return ans;
	}

	/**
	 * 
	 * @param timeInMillis
	 *        time in milliseconds
	 * @return
	 *        A custom format of the given time: "EEE, d MMM yyyy HH:mm:ss"
	 */
	private static String formatAGivenTime(long timeInMillis) {
		final Date date = new Date();
		date.setTime(timeInMillis);	
		return new SimpleDateFormat(SystemLogger.MESSAGE_DEFAULT_DATE_FORMAT).format(date);
	}

	/**
	 * Initializes the logger in case it doesn't exist, and adds the given message to the log file
	 * 
	 * In case an error occurred, nothing will happen
	 * 
	 * @param message
	 * 		  A message to log in
	 * @param level
	 * 		The message priority level (will appear in the log file)
	 */
	private static void logAMessage(String message, Level level ) {
		if (SystemLogger.LOGGER == null) {
			if (createLogger())
				SystemLogger.LOGGER.log(level, message);
			// TODO: consider send a failure e-mail to the forum admin
		}
		else
			SystemLogger.LOGGER.log(level, message);	
	}

	/**
	 * Logs a message with a FINE level
	 * 
	 * @param message
	 * 		The message which should be logged
	 */
	public static void fine(String message) {
		SystemLogger.logAMessage(message, Level.FINE);
	}

	/**
	 * Logs a message with an INFO level
	 * 
	 * @param message
	 * 		The message which should be logged
	 */
	public static void info(String message) {
		SystemLogger.logAMessage(message, Level.INFO);
	}

	/**
	 * Logs a message with a WARNING level
	 * 
	 * @param message
	 * 		The message which should be logged
	 */
	public static void warning(String message) {
		SystemLogger.logAMessage(message, Level.WARNING);
	}

	/**
	 * Logs a message with a SEVERE level
	 * 
	 * @param message
	 * 		The message which should be logged
	 */
	public static void severe(String message) {
		SystemLogger.logAMessage(message, Level.SEVERE);
	}

	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/
	public static void addFileHandler(FileHandler handler) {
		SystemLogger.LOGGER.addHandler(handler);
	}




	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/

	/**
	 * Changes the logging mode to file only, the logging won't be done to the CLI
	 * 
	 * @return
	 * 		True if the mode changing succeeded and false otherwise
	 */
	public static boolean switchToOnlyFileLogMode() {
		SystemLogger.disableConsoleLogging();
		try {
			SystemLogger.setFileLogging();
		}
		catch (IOException e){
			return false;
		}
		return true;
	}
}