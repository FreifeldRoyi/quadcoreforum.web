/**
 * 
 */
package forum.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author sepetnit
 *
 */
public class ActiveConnectedData implements Serializable {

	private static final long serialVersionUID = -1473196384657990003L;

	private Collection<String> activeUsernames;
	
	private long guestsNumber;
	
	public ActiveConnectedData() { }
	
	public ActiveConnectedData(long guestsNumber, Collection<String> connected) {
		setGuestsNumber(guestsNumber);
		setActiveUsernames(connected);
	}

	/**
	 * @return the activeUsernames
	 */
	public Collection<String> getActiveUsernames() {
		return activeUsernames;
	}

	/**
	 * @param activeUsernames the activeUsernames to set
	 */
	public void setActiveUsernames(Collection<String> activeUsernames) {
		this.activeUsernames = new ArrayList<String>(activeUsernames);
	}

	/**
	 * @return the guestsNumber
	 */
	public long getGuestsNumber() {
		return guestsNumber;
	}

	/**
	 * @param guestsNumber the guestsNumber to set
	 */
	public void setGuestsNumber(long guestsNumber) {
		this.guestsNumber = guestsNumber;
	}
}
