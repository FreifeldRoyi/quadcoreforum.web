/**
 * This exception occurs when a user which doesn't have a permission to perform an operation asks to perform
 * this operation 
 */
package forum.server.domainlayer.message;

import forum.server.domainlayer.user.Permission;

/**
 * @author sepetnit
 *
 */
public class NotPermittedException extends Exception {
	
	private static final long serialVersionUID = -1239416816486707410L;

	/**
	 * The exception constructor, constructs an exception with the suitable message, which informs the user about
	 * the unpermitted operation
	 * 
	 * @param userID
	 * 		The id of the user who asked the operation
	 * @param permission
	 * 		The permission which the user should have in order to perform the asked operation
	 */
	public NotPermittedException(long userID, Permission permission) {
		super("A user with id " + userID + " doesn't have the permission " + permission);
	}
}