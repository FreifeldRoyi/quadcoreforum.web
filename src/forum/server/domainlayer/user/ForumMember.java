/**
 * This class represents a member of the forum (a registered user)
 */
package forum.server.domainlayer.user;

import java.util.*;

import forum.server.domainlayer.interfaces.UIMember;

/**
 * @author Freifeld Royi
 *
 */
public class ForumMember extends ForumUser implements UIMember {
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;

	private Collection<Long> postedMessagesIDs;

	/**
	 * A constructor of a member, which initializes all the member attributes according to the given parameters
	 * 
	 * This constructor is used when a new member object is constructed according to an existing member (from the database) 
	 * 
	 * @param userID
	 * 		The user id of the created member
	 * @param username
	 * 		The user-name of the created member
	 * @param password
	 * 		The password of the created member
	 * @param lastName
	 * 		The last name of the created member
	 * @param firstName
	 * 		The first name of the created member
	 * @param email
	 * 		The e-mail address of the created member
	 * @param permissions
	 * 		The permissions which are assigned to the created member
	 * @param postedMessagesIDs
	 * 		A collection of the messages ids which were posted by this member
	 */
	public ForumMember(final long userID, final String username, final String password, 
			final String lastName, final String firstName, final String email, final Collection<Permission> permissions, 
			final Collection<Long> postedMessagesIDs) {
		super(userID, permissions);
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.postedMessagesIDs = new Vector<Long>();
		if (postedMessagesIDs != null)
			this.postedMessagesIDs.addAll(postedMessagesIDs);
	}

	/**
	 * A full constructor of a member, hasn't exist yet in the database and therefore some of his attributes are initialized to
	 * default values
	 *  
	 * @param userID
	 * 		The user id of the created member
	 * @param username
	 * 		The user-name of the created member
	 * @param password
	 * 		The password of the created member
	 * @param lastName
	 * 		The last name of the created member
	 * @param firstName
	 * 		The first name of the created member
	 * @param email
	 * 		The e-mail address of the created member
	 * @param permissions
	 * 		The permissions which are assigned to the created member
	 */
	public ForumMember(final long userID, final String username, final String password, final String lastName, 
			final String firstName, final String email, final Collection<Permission> permissions) {
		super(userID, permissions);
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.postedMessagesIDs = new Vector<Long>();
	}
	
	// getters
	
	/**
	 * @see
	 * 		UIMember#getUsername()
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * 
	 * @return
	 * 		The password of the member 
	 * 
	 * Note: The returned password is encrypted for security reasons 
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @see
	 * 		UIMember#getLastName()
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @see
	 * 		UIMember#getFirstName()
	 */
	public String getFirstName() {
		return this.firstName;
	}
	
	/**
	 * @see
	 * 		UIMember#getEmail()
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @see
	 * 		UIMember#getPostsNumber()
	 */
	public int getPostsNumber() {
		return this.postedMessagesIDs.size();
	}

	// setters

	/**
	 * Sets the last name of the member to be the given one
	 * 
	 * @param lastName
	 * 		The last name to which the member's last name should be set
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets the first name of the member to be the given one
	 * 
	 * @param firstName
	 * 		The first name to which the member's first name should be set
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the member password to be the given one
	 * 
	 * @param password
	 * 		A new encrypted password to which the member's password should be set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	// methods
	
	/**
	 * Adds a new message id to the member's posts container
	 * 
	 * @param messageID
	 * 		The id of the message which was posted by this member and therefore
	 * 		should be added to his posts container 
	 */
	public void addPostedMessage(final long messageID) {
		this.postedMessagesIDs.add(messageID);
	}

	/**
	 * Removes a message from the member's posts container
	 * 
	 * @param messageID
	 * 		The id of the message which should be removed from the member's posts
	 * 		container
	 */
	public void removePostedMessage(final long messageID) {
		this.postedMessagesIDs.remove(messageID);
	}

	/**
	 * @see
	 * 		UIMember#toString()
	 */
	public String toString() {
		return this.username + "\t" + this.lastName + "\t" + this.firstName;
	}
}
