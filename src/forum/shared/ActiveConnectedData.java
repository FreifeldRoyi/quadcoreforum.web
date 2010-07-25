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

	private Collection<String> activeNames;
	
	private long guestsNumber;
	
	public ActiveConnectedData() { }
	
	public ActiveConnectedData(long guestsNumber, Collection<String> connected) {
		setGuestsNumber(guestsNumber);
		setActiveNames(connected);
	}

	/**
	 * @return the activeUsernames
	 */
	public Collection<String> getActiveNames() {
		return activeNames;
	}

	/**
	 * @param activeUsernames the activeUsernames to set
	 */
	public void setActiveNames(Collection<String> activeUsernames) {
		this.activeNames = new ArrayList<String>(activeUsernames);
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
