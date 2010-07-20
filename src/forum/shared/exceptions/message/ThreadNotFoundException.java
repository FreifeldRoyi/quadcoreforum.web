/**
 * 
 */
package forum.shared.exceptions.message;

import java.io.Serializable;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class ThreadNotFoundException extends Exception implements Serializable {

	private static final long serialVersionUID = -6083962457516364040L;

	public ThreadNotFoundException(long threadID) {
		super("A thread with the id " + threadID + " wasn't found!");
	}
}
