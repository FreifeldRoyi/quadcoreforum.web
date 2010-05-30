/**
 * This class is responsible for managing users authorization, login and registration methods, and all other operations related to
 * users handling
 */

package forum.server.domainlayer.user ;

import java.util.*;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.ForumDataHandler;
import forum.server.domainlayer.ForumFacade;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;

import forum.server.domainlayer.interfaces.*;
import forum.server.domainlayer.message.NotPermittedException;

import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

public class UsersController {
	// the handler through which the class accesses the cache instances through which the operations
	// of persistence are performed
	private ForumDataHandler dataHandler;
	// the current number of guests connected to the forum
	private long guestsCounter;
	// stores a set of all the user-names of members currently connected to the forum
	private Collection<String> activeMembersUserNames;
	
	/**
	 * The class constructor.
	 * 
	 * Creates and initializes a new users' controller.
	 */
	public UsersController(ForumDataHandler dataHandler) {
		this.dataHandler = dataHandler;
		this.guestsCounter = 0;
		this.activeMembersUserNames = new HashSet<String>();
	}

	// Guest related methods

	/**
	 * @see
	 * 		ForumFacade#getActiveGuestsNumber()
	 */
	public long getActiveGuestsNumber() {
		return this.guestsCounter;
	}

	/**
	 * @see
	 * 		ForumFacade#addGuest()
	 */
	public UIUser addGuest() {
		SystemLogger.fine("A new guest has connected to the forum");
		this.incActiveGuestsCounter();
		final Collection<Permission> permissions = this.getDefaultGuestPermissions();
		return this.dataHandler.getUsersCache().createNewGuest(permissions);
	}
	
	/**
	 * @see
	 * 		ForumFacade#removeGuest(long)
	 */
	public void removeGuest(final long guestID) {
		SystemLogger.fine("The guest with id " + guestID + " tries to exit from the forum.");
		try {
			this.dataHandler.getUsersCache().removeGuest(guestID);
			this.decActiveGuestsCounter();
			SystemLogger.fine("The guest with id " + guestID + " has successfuly been removed from the forum.");
		}
		catch (NotRegisteredException e) {
			SystemLogger.fine("Error occured: a guest with an id " + guestID + " wasn't found in the system.");
		}
	}

	/**
	 * Increases the counter of the guests currently connected to the forum
	 */
	private void incActiveGuestsCounter() {
		this.guestsCounter++;
	}

	/**
	 * Decreases the counter of guests currently connected to the forum
	 */
	private void decActiveGuestsCounter() {
		this.guestsCounter--;
	}

	// User related methods

	/**
	 * 
	 * @return
	 * 		A collection of all the user-name of members which are currently active
	 * 		(logged-in to the forum)
	 */
	public Collection<String> getActiveMemberNames() {
		return this.activeMembersUserNames;
	}

	/**
	 * 
	 * @see
	 * 		ForumFacade#getAllMembers()
	 */
	public Collection<UIMember> getAllMembers() throws DatabaseRetrievalException {
		SystemLogger.info("A User requests to view all the forum members.");
		Collection<UIMember> toReturn = new Vector<UIMember>();
		toReturn.addAll(this.dataHandler.getUsersCache().getAllMembers());
		SystemLogger.info("A forum members were retrieved and returned.");
		return toReturn;
	}
	
	/**
	 * @see
	 * 		ForumFacade#getMemberIdByUsername(String)
	 */
	public long getMemberIdByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException {
		ForumMember tMember = this.dataHandler.getUsersCache().getMemberByUsername(username);
		if (tMember == null)
			throw new NotRegisteredException(username);
		else
			return tMember.getID();
	}

	/**
	 * @see	ForumFacade#registerNewMember(String, String, String, String, String)
	 */
	public long registerNewMember(final String username, final String password, final String lastName,
			final String firstName, final String email) throws MemberAlreadyExistsException, DatabaseUpdateException {
		SystemLogger.info("A User requests to register with username " + username);
		final Collection<Permission> tPermissions = this.getDefaultMemberPermissions();
		final String tEncryptedPassword = this.encryptPassword(password);
		final ForumMember newMember = this.dataHandler.getUsersCache().createNewMember(username, tEncryptedPassword, lastName, 
				firstName, email, tPermissions);
		SystemLogger.info("New member with username " + username + " has successfuly been registered");
		return newMember.getID();
	}

	/**
	 * 
	 * Encrypts the given password in order to check its validity against the stored one
	 * 
	 * @param password
	 * 		The password which should be encrypted
	 * @return
	 * 		The encrypted password
	 */
	private String encryptPassword(final String password) {
		return PasswordEncryptor.encryptMD5(password);
	}
	
	/**
	 * 
	 * Encrypts the entered password according to a chosen encryption algorithm and checks
	 * whether the encrypted password is valid, against the given real password 
	 * 
	 * @param enteredPassword
	 * 		The password which was entered by the user
	 * @param realPassword
	 * 		The real password to which the entered password is compared
	 * @inv 
	 * 		enteredPassword != null && realPassword != null
	 * 
	 * @return
	 * 		True if the entered password is valid and false otherwise
	 */
	private boolean checkPasswordValidity(final String enteredPassword, final String realPassword) {
		return realPassword.equals(this.encryptPassword(enteredPassword));
	}

	/**
	 * Adds a new user-name of a member who has been logged-in to the forum
	 *  
	 * @param usernameToAdd
	 * 		The user-name of the member which should be added to the active members collection
	 */
	private void addActiveMemberUsername(String usernameToAdd) {
		this.activeMembersUserNames.add(usernameToAdd);
	}

	/**
	 * Removes a user-name of a member who has been logged-out from the forum
	 * 
	 * @param usernameToRemove
	 * 		The user-name of the member which should be removed from the active
	 * 		members collection
	 */
	private void removeActiveMemberUsername(String usernameToRemove) {
		this.activeMembersUserNames.remove(usernameToRemove);
	}	

	/**
	 * @see
	 * 		ForumFacade#login(String, String)
	 */
	public UIMember login(final String username, final String password) throws NotRegisteredException, 
	WrongPasswordException, DatabaseRetrievalException {
		SystemLogger.fine("A member with username " + username + " tries to log-in");
		final ForumMember tMemberToLogIn = this.dataHandler.getUsersCache().getMemberByUsername(username);
		if (tMemberToLogIn == null) {
			SystemLogger.fine("A member with username " + username + " doesn't exist, can't log-in");
			throw new NotRegisteredException(username);
		}
		if (!this.checkPasswordValidity(password, tMemberToLogIn.getPassword())) {
			SystemLogger.fine("Can't log-in a member with username " + username + " because a wrong password was given");
			throw new WrongPasswordException();
		}
		this.addActiveMemberUsername(username);
		return tMemberToLogIn;
	}

	/**
	 * @see
	 * 		ForumFacade#logout(String)
	 */
	public void logout(final String username) throws NotConnectedException {
		SystemLogger.fine("A user with username " + username + " requests to log-out the forum");
		if (this.activeMembersUserNames.contains(username)) {
			this.removeActiveMemberUsername(username);
			SystemLogger.info("The member with username " + username + " has logged-out from the forum");
		}
		else throw new NotConnectedException(username);
	}

	/**
	 * 
	 * Checks whether the given user is a guest of the forum
	 * 
	 * @param userToCheck
	 *		The user which should be checked to be a forum guest 
	 * 
	 * @return
	 * 		True if the given user is forum guest and false otherwise
	 */
	private boolean isGuest(ForumUser userToCheck) {
		return (userToCheck != null) && (userToCheck.getID() < 0);
	}
	
	/**
	 * @see
	 * 		ForumFacade#promoteToBeModerator(long, long)
	 */
	public void promoteToBeModerator(final long applicantID, final String username) throws NotPermittedException, 
	NotRegisteredException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + applicantID + " requests to promote a user " +
					username + " to be forum moderator.");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(applicantID);
			if (tApplicant.isAllowed(Permission.SET_MODERATOR)) {
				SystemLogger.info("Permission granted for user " + applicantID + ".");
				final ForumUser tForumUser = this.dataHandler.getUsersCache().getMemberByUsername(username);
				System.out.println(this.getDefaultModeratorPermissions());
				tForumUser.setPermissions(this.getDefaultModeratorPermissions());
				this.dataHandler.getUsersCache().updateInDatabase(tForumUser);
				SystemLogger.info("The user with " + username + " has been successfully promoted to be a " +
						"moderator of the forum.");
			}
			else {
				SystemLogger.info("unpermitted operation for user " + applicantID + ".");
				throw new NotPermittedException(applicantID, Permission.SET_MODERATOR);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
	
	// Default permissions methods
	
	/**
	 * 
	 * @return
	 * 		A default permissions for the guests of the forum.
	 */
	private Collection<Permission> getDefaultGuestPermissions() {
		final Collection<Permission> toReturn = new HashSet<Permission>();
		toReturn.add(Permission.VIEW_ALL);
		return toReturn;
	}
	
	/**
	 * 
	 * @return
	 * 		A default permission set for a forum-member user, the set contains all guest permissions
	 * 		and additional permissions specified for the registered users of the forum.
	 */
	private Collection<Permission> getDefaultMemberPermissions() {
		final Collection<Permission> toReturn = this.getDefaultGuestPermissions();
		toReturn.add(Permission.OPEN_THREAD);
		toReturn.add(Permission.REPLY_TO_MESSAGE);
		toReturn.add(Permission.EDIT_MESSAGE);
		return toReturn;
	}
	
	/**
	 * 
	 * @return
	 * 		A default permission set for a forum-moderator user, the set contains all member permissions
	 * 		and additional permissions specified for the moderators of the forum.
	 */
	private Collection<Permission> getDefaultModeratorPermissions() {
		final Collection<Permission> toReturn = this.getDefaultMemberPermissions();
		toReturn.add(Permission.DELETE_THREAD);
		toReturn.add(Permission.DELETE_MESSAGE);
		toReturn.add(Permission.SET_MODERATOR);
		return toReturn;
	}
}
