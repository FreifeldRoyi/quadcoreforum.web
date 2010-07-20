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

	public NotRegisteredException(String username) {
		super("A user with a username " + username + " is not registered!");
	}
	
	public NotRegisteredException(long userID) {
		super("A user with an id " + userID + " is not registered!");
	}
	
	
}
