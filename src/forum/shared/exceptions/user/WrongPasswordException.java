/**
 * 
 */
package forum.shared.exceptions.user;

import java.io.Serializable;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class WrongPasswordException extends Exception implements Serializable {
	
	private static final long serialVersionUID = 2739339709596637315L;

	public WrongPasswordException() {
		super("The given password is wrong");
	}
}
