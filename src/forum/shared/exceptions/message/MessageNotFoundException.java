/**
 * 
 */
package forum.shared.exceptions.message;

import java.io.Serializable;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class MessageNotFoundException extends Exception implements Serializable {

	private static final long serialVersionUID = 1000753555749212018L;

	public MessageNotFoundException(long messageID) {
		super("A message with an id " + messageID + " was not found!");
	}
	
	@Override
	public String getMessage() {
		return "The message wasn't found! Maybe it was deleted by another user";
	}
}
