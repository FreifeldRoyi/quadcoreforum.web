/**
 * 
 */
package forum.shared.exceptions.user;

import java.io.Serializable;

/**
 * @author sepetnit
 *
 */
public class NotConnectedException extends Exception implements Serializable {

	private static final long serialVersionUID = 2483803467027074113L;
	
	private String username;

	private long userID;

	public NotConnectedException() { }
	public NotConnectedException(String username) {
		super("A user with a username " + username + " isn't connected!");
		this.username = username;
	}

	public NotConnectedException(long userID) {
		super("A user with an id " + userID + " isn't connected!");
		this.userID = userID;
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
