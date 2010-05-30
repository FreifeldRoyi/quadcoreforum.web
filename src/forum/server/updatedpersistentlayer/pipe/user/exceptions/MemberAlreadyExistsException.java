/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.user.exceptions;

/**
 * @author sepetnit
 *
 */
public class MemberAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -4533139714961245124L;

	public MemberAlreadyExistsException(String message) {
		super(message);
	}
}
