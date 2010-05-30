/**
 * This class is connected to the persistent layer through the two caches - users and messages cache.
 * 
 * The class is responsible of initializing of the connection to the database and contains getters to the
 * cache memories of the forum through which database related operations are performed
 */
package forum.server.domainlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.message.*;
import forum.server.domainlayer.user.*;
import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;

/**
 * @author Vitali Sepetnitsky
 *
 */
public class ForumDataHandler {
	// the initial user-name of the forum administrator which will be given it when the forum is initialized 
	private static final String ADMIN_INIITIAL_USERNAME = "admin";
	private static final String ADMIN_ENCRYPTED_PASSWORD = PasswordEncryptor.encryptMD5("1234");
	private static final String ADMIN_EMAIL = "qcforuminfo@gmail.com";
	private static final Set<Permission> ADMIN_PERMISSIONS = new HashSet<Permission>();
	
	static { // initializes administrator permissions
		ForumDataHandler.ADMIN_PERMISSIONS.addAll(Arrays.asList(Permission.values()));
	}	
	
	// this cache is responsible of handling the users connected to the forum
	private final UsersCache usersCache;
	// this cache is responsible if handling the forum subjects, threads and messages
	private final MessagesCache messagesCache;
	
	// the forum administrator
	private ForumMember admin;
	
	/**
	 * The class constructor.
	 * 
	 * Initializes the caches which are connected to the database through the persistent layer.
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case a connection with the forum database can't be established
	 * @throws DatabaseUpdateException
	 * 		In case a connection error with the database has occurred while trying to update it with the new admin
	 * 		details
	 */
	public ForumDataHandler() throws DatabaseRetrievalException, DatabaseUpdateException {
		SystemLogger.info("Initializes cache memories");
		this.usersCache = new UsersCache();
		this.messagesCache = new MessagesCache();
		SystemLogger.info("Cache memories have been initializes successfuly");	
		this.initializeAdmin();
	}
	
	/**
	 * Initializes the forum administrator details to the initial details.
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case a connection error to the database has occurred while trying to update it with the new admin
	 * 		details
	 */
	private void initializeAdmin() throws DatabaseUpdateException {
		SystemLogger.info("Building initial administrator");
		try {
			this.admin = this.getUsersCache().createNewMember(ForumDataHandler.ADMIN_INIITIAL_USERNAME, 
					ForumDataHandler.ADMIN_ENCRYPTED_PASSWORD, ForumDataHandler.ADMIN_INIITIAL_USERNAME,
					ForumDataHandler.ADMIN_INIITIAL_USERNAME,
					ForumDataHandler.ADMIN_EMAIL,
					ForumDataHandler.ADMIN_PERMISSIONS);
		}
		catch (MemberAlreadyExistsException e) {
			SystemLogger.info("Admin was previously created in the database");
		}
	}	
	
	/**
	 * 
	 * @return
	 * 		The cache memory which handles the database operations of the forum
	 * 		users, through the persistent layer
	 */
	public UsersCache getUsersCache() {
		return this.usersCache;
	}
	
	/**
	 * 
	 * @return
	 * 		The cache memory which handles the database operation of the forum 
	 * 		content (subjects, messages and threads) through the persistent layer
	 */
	public MessagesCache getMessagesCache() {
		return this.messagesCache;
	}
}
