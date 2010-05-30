/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.message.exceptions;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class ThreadNotFoundException extends Exception {

	private static final long serialVersionUID = -6083962457516364040L;

	public ThreadNotFoundException(long threadID) {
		super("A thread with the id " + threadID + " wasn't found!");
	}
}
