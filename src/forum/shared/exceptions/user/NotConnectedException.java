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
	
	public NotConnectedException(String username) {
		super("A user with a username " + username + " isn't connected!");
	}

	public NotConnectedException(long userID) {
		super("A user with an id " + userID + " isn't connected!");
	}

}
