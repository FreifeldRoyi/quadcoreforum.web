/**
 * This class is responsible of handling all the operations related to the forum users
 * which requires a communication to the persistent layer
 * 
 * In addition this class serves as a cache memory which holds users repository and therefore it is responsible
 * of storing the forum active guests in the repository
 */

package forum.server.domainlayer.user;

import java.util.*;

import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.PersistenceDataHandler;
import forum.server.updatedpersistentlayer.pipe.PersistenceFactory;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class UsersCache {

	// The next id which can be assigned to a new client who connects to the forum
	// the guest ids will be negative
	private long nextFreeGuestID;
	// The next id which can be assigned to a new member of the forum
	// the members ids will be positive
	private long nextFreeMemberID;
	private final Map<Long, ForumUser> idsToUsersMapping;
	private final PersistenceDataHandler pipe; // A pipe to the persistence layer


	/**
	 * The class constructor. 
	 * 
	 * Creates and initializes a new users cache.
	 */
	public UsersCache() throws DatabaseRetrievalException {
		this.pipe = PersistenceFactory.getPipe();
		this.nextFreeGuestID = -2;
		this.nextFreeMemberID = this.pipe.getFirstFreeMemberID();
		this.idsToUsersMapping = new HashMap<Long, ForumUser>();
	}

	/**
	 * 
	 * @return
	 * 		A free id number that can be assigned to a new member of the forum, who wants to
	 * 		register (the method promises that the returned id hasn't been assigned to an existing member)
	 */
	private long getNextFreeMemberID() {
		// TODO: Make this to be synchronized in order to prevent two members with the same id
		return this.nextFreeMemberID++;
	}

	/**
	 * 
	 * @return
	 * 		The next id which can be assigned to a guest
	 */
	private long getNextGuestID() {
		// TODO: Make this to be synchronized in order to prevent two guests with the same id
		return this.nextFreeGuestID--;
	}

	/**
	 * 
	 * Finds and returns a user whose id equals to the given one (this user can be a guest or a forum member)
	 * 
	 * @param id
	 * 		The id of the user which should be found and returned
	 * 
	 * @return
	 * 		The found user
	 * 
	 * @throws NotRegisteredException
	 * 		If a user with the given id doesn't exists in the forum - neither as a member nor as a guest
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the forum database due to a database connection error
	 */
	public ForumUser getUserByID(final long id) throws NotRegisteredException, DatabaseRetrievalException {
		if (this.containsUser(id))
			return this.idsToUsersMapping.get(id);
		else
			return this.pipe.getUserByID(id);
	}	

	/**
	 * 
	 * Finds and returns a member whose user-name equals to the given one
	 * The assumption is that the forum members user-names are unique.
	 *  
	 * @param username
	 * 		The user-name of the member which should be found and returned
	 * 
	 * @return
	 * 		The found member or null if no member with the given user-name is registered to the forum
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the forum database due to a database connection error
	 */
	public ForumMember getMemberByUsername(final String username) throws DatabaseRetrievalException {
		try {
			// TODO: Handle cache saving
			ForumMember toReturn = this.pipe.getMemberByUsername(username);
			return toReturn;
		}
		catch (NotRegisteredException e) {
			return null;
		}
	}

	/**
	 * 
	 * Finds and returns a member whose e-mail equals to the given one
	 * The assumption is that the forum members e-mails are unique.
	 * 
	 * @param e-mail
	 * 		The e-mail of the member which should be found and returned
	 * 
	 * @return
	 * 		The found member or null if no member with the given e-mail is registered to the forum
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the forum database due to a database connection error
	 */
	public ForumMember getMemberByEmail(final String email) throws DatabaseRetrievalException {
		try {
			// TODO: Handle cache saving
			ForumMember toReturn = this.pipe.getMemberByEmail(email);
			return toReturn;
		}
		catch (NotRegisteredException e) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 * 		A collection of all users of the forum - guests and members
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the forum database due to a database connection error
	 */
	public Collection<ForumUser> getAllUsers() throws DatabaseRetrievalException {
		final Set<ForumUser> toReturn = new HashSet<ForumUser>();
		toReturn.addAll(this.idsToUsersMapping.values());
		toReturn.addAll(this.getAllMembers());
		return toReturn;
	}

	/**
	 * 
	 * @return
	 * 		A collection of all the members of the forum
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the forum database due to a database connection error
	 */
	public Collection<ForumMember> getAllMembers() throws DatabaseRetrievalException {
		return this.pipe.getAllMembers();
	}
	
	/**
	 * Returns whether the cache contains an already loaded user with the given id
	 * 
	 * @param id
	 * 		The id of the user which should be searched
	 * @return
	 * 		Whether a user with the given id is already loaded in to the cache
	 */
	private boolean containsUser(final long id) {
		return this.idsToUsersMapping.containsKey(id);
	}

	/**
	 * 
	 * Creates new forum guest
	 * 
	 * @param permissions
	 * 		The collection of permissions which should be assigned to the new guest
	 * 
	 * @return
	 * 		The created guest
	 */
	public ForumUser createNewGuest(final Collection<Permission> permissions) {
		final long tID = this.getNextGuestID();
		final ForumUser tNewUser = new ForumUser(tID, permissions);
		this.idsToUsersMapping.put(tID, tNewUser);
		return tNewUser;
	}

	/**
	 * Removes a guest with the given id from the cache.
	 * 
	 * @param id
	 * 		The id of the guest which should be removed from the cache
	 * 
	 * @throws NotRegisteredException
	 * 		In case a guest with the given id doesn't exist in the cache memory
	 */
	public void removeGuest(final long id) throws NotRegisteredException {
		if (this.idsToUsersMapping.containsKey(id))			
			this.idsToUsersMapping.remove(id);
		else throw new NotRegisteredException(id);
	}
	
	/**
	 * 
	 * Creates a new member in the forum community according to the given attributes and registers it in the forum 
	 * database
	 * 
	 * @param username
	 * 		The user-name of the new member
	 * @param password
	 * 		The password of the new member
	 * @param lastName
	 * 		The last name of the new member
	 * @param firstName
	 * 		The first name of the new member
	 * @param email
	 * 		The email of the new member
	 * @param permissions
	 * 		A collection of permissions which should be assigned to the new member
	 * 
	 * @return
	 * 		The created member object which was registered to the forum
	 * 
	 * @throws MemberAlreadyExistsException
	 * 		In case a member with the given details has been already registered to the forum 
	 * @throws DatabaseUpdateException
	 * 		In case the forum database can't be updated with the details of the new member due to a 
	 * 		database update error
	 */
	public ForumMember createNewMember(final String username, final String password, final String lastName,
			final String firstName, final String email, final Collection<Permission> permissions) throws 
			MemberAlreadyExistsException, DatabaseUpdateException {
		try {
			if ((this.getMemberByUsername(username) != null))
				throw new MemberAlreadyExistsException(username);
			else if (this.getMemberByEmail(email) != null)
				throw new MemberAlreadyExistsException(email); // TODO: throw e-mail already exists exception
		}
		catch (DatabaseRetrievalException e) {
			e.printStackTrace();
			throw new DatabaseUpdateException();
		}		
		final long id = this.getNextFreeMemberID();
		// the new member password should be saved encrypted even in the cache
		final ForumMember newMember = new ForumMember(id, username, PasswordEncryptor.encryptMD5(password),
				lastName, firstName, email, permissions);
		this.pipe.addNewMember(newMember.getID(), username, password, lastName, firstName, email, permissions);	
		this.idsToUsersMapping.put(id, newMember);
		return newMember;
	}
	
	/**
	 * 
	 * Updates the data of the given user of the forum in the forum database
	 * 
	 * @param updatedUser
	 * 		The updated user whose data should be updated in the database
	 * 
	 * @throws NotRegisteredException
	 * 		In case a user with the given id hasn't been found in the database
	 * @throws DatabaseUpdateException
	 * 		In case the database can't be updated due to a database connection error
	 */
	public void updateInDatabase(ForumUser updatedUser) throws NotRegisteredException, DatabaseUpdateException {
		// removes the user from the cache in order to allow the changes only after the next login and not right
		// now. Therefore the user isn't updated in the cache but only via the database
		this.idsToUsersMapping.remove(updatedUser.getID());
		this.pipe.updateUser(updatedUser.getID(), updatedUser.getPermissions());
	}

}