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

	private static final long serialVersionUID = -1213520875618447542L;

	private long messageID;
	
	public MessageNotFoundException() { }
	
	public MessageNotFoundException(long messageID) {
		super("A message with an id " + messageID + " was not found!");
		this.messageID = messageID;
	}
	
	@Override
	public String getMessage() {
		return "The message wasn't found! Maybe it was deleted by another user";
	}

	public long getID() {
		return this.messageID;
	}
	
	public void setID(long messageID) {
		this.messageID = messageID;
	}
}
