/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.user.exceptions;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class WrongPasswordException extends Exception {
	
	private static final long serialVersionUID = 2739339709596637315L;

	public WrongPasswordException() {
		super("The given password is wrong");
	}
}
