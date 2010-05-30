/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.user.exceptions;

/**
 * @author sepetnit
 *
 */
public class AlreadyConnectedException extends Exception {

	private static final long serialVersionUID = -3193246183594257892L;

	public AlreadyConnectedException(String username) {
		super("A user with a username " + username + " is already connected!");
	}
}
