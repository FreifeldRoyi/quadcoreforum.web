/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe;

import forum.server.updatedpersistentlayer.SessionFactoryUtil;



/**
 * @author sepetnit
 *
 */


public class PersistenceFactory 
{
	private static PersistenceDataHandler PERSISTENCE_DATA_HANDLER = null;

	/**
	 * Initializes the forum database (in case it hasn't initialized yet, and returns a pipe which
	 * handles the database operations)
	 */
	public static PersistenceDataHandler getPipe() {
		if (PERSISTENCE_DATA_HANDLER == null) {
				PERSISTENCE_DATA_HANDLER = SQLpersistenceDataHandler.getInstance();
		}
		return PERSISTENCE_DATA_HANDLER;
	}
	
	public static void closeDatabaseConnection() {
		SessionFactoryUtil.close();
	}
}

