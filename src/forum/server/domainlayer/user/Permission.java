package forum.server.domainlayer.user;

/**
 * This enumeration is responsible to declare the available permissions which determine what are the access 
 * permissions of the forum users, what the users are allowed to do and what not.
 * 
 * For example, guests will be disallowed to post or hand out moderating powers. 
 * 
 */

/**
 * 
 * @author Sepetnitsky Vitali
 *
 */
public enum Permission {
	
	/* View all the forum content */
	VIEW_ALL,
	
	/* Add new subject to the forum root tree */
	ADD_SUBJECT,
	
	/* Add new sub-subject to new subject */
	ADD_SUB_SUBJECT,

	/* Edit a new subject */
	EDIT_SUBJECT, 
	
	/* Delete a full subject */
	DELETE_SUBJECT,

	/* Open new thred under a subject */
	OPEN_THREAD,
	
	/* Delete the full thread (with all its contents) */
	DELETE_THREAD,
	
	/* Reply to a message */
	REPLY_TO_MESSAGE,
	
	/* Edit a message */
	EDIT_MESSAGE,
	
	/* Delete a message (all its replies will be deleted recursively */
	DELETE_MESSAGE,
	
	/* Make a forum user to be moderator */
	SET_MODERATOR
}
