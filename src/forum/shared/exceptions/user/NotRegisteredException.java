/**
 * 
 */
package forum.shared.exceptions.user;

import java.io.Serializable;

/**
 * @author sepetnit
 *
 */
public class NotRegisteredException extends Exception implements Serializable {

	private static final long serialVersionUID = 3459306523924172336L;

	private String username;
	private long userID;

	public NotRegisteredException() { }
	public NotRegisteredException(String username) {
		super("A user with a username " + username + " is not registered!");
	}
	
	public NotRegisteredException(long userID) {
		super("A user with an id " + userID + " is not registered!");
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
}
