/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.user.exceptions;

/**
 * @author sepetnit
 *
 */
public class NotConnectedException extends Exception {

	private static final long serialVersionUID = 2483803467027074113L;
	
	public NotConnectedException(String username) {
		super("A user with a username " + username + " isn't connected!");
	}
}
