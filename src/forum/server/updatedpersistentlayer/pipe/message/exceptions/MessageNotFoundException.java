/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.message.exceptions;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class MessageNotFoundException extends Exception {

	private static final long serialVersionUID = 1000753555749212018L;

	public MessageNotFoundException(long messageID) {
		super("A message with an id " + messageID + " was not found!");
	}
}
