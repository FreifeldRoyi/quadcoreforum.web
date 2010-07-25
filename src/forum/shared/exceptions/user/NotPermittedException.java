/**
 * This exception occurs when a user which doesn't have a permission to perform an operation asks to perform
 * this operation 
 */
package forum.shared.exceptions.user;

import java.io.Serializable;

import forum.shared.Permission;


/**
 * @author sepetnit
 *
 */
public class NotPermittedException extends Exception implements Serializable {

	private static final long serialVersionUID = 5108237284785292689L;
	
	private long userID;

	private Permission permission;
	
	public NotPermittedException() { }
	
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
		this.userID = userID;
		this.permission = permission;
	}
	
	/**
	 * @return the userID
	 */
	public long getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(long userID) {
		this.userID = userID;
	}

	/**
	 * @return the permission
	 */
	public Permission getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
}