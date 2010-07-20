/**
 * 
 */
package forum.shared.exceptions.user;

import java.io.Serializable;

/**
 * @author sepetnit
 *
 */
public class MemberAlreadyExistsException extends Exception implements Serializable {

	private static final long serialVersionUID = -4533139714961245124L;

	public MemberAlreadyExistsException(String message) {
		super(message);
	}
}
