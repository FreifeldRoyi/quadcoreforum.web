package forum.shared.tcpcommunicationlayer;

import java.io.Serializable;

/**
 * This is class represents a server response message sent to the client.<br>
 * (A response is sent after a message is received from the client).
 * 
 * @author Tomer Heber
 */
public class ServerResponse implements Serializable {

	private static final long serialVersionUID = -736273246091582824L;

	private String m_response;
	private boolean m_hasExecuted;
	
	private boolean guestIDChanged;
	private boolean memberUsernameChanged;

	private long connectedGuestID;
	private String loggedInUsername;

	public ServerResponse() {}
	
	public ServerResponse(String response, boolean hasExecuted) {
		m_response = response;
		m_hasExecuted = hasExecuted;

		guestIDChanged = false;
		memberUsernameChanged = false;

		connectedGuestID = -1;
		loggedInUsername = "";
	}

	/**
	 * @return Returns the response message sent by the server.
	 */
	public String getResponse() {
		return m_response;
	}

	/**
	 * The response also tells us if a message that we sent to the server was indeed successfully
	 * executed by the server.
	 * 
	 * @return True if a command message sent by the client was executed successfully by the server.	 
	 */
	public boolean hasExecuted() {
		return m_hasExecuted;
	}
	/**
	 * setter to the m_hasExecuted class member
	 * @param execflag - value for the HasExecuted class member. 
	 */
	public void setHasExecuted(boolean execflag){
		m_hasExecuted = execflag;
	}

	/**
	 * setter to the m_response class member
	 * @param response -value for the response class member.
	 */
	public void setResponse(String response){
		m_response = response;
	}

	public boolean guestIDChanged() {
		return this.guestIDChanged;
	}

	public void setGuestIDChanged() {
		this.guestIDChanged = true;
	}

	public long getConnectedGuestID() {
		return this.connectedGuestID;
	}

	public void setConnectedGuestID(long id) {
		this.connectedGuestID = id;
	}


	public boolean memberUsernameChanged() {
		return this.memberUsernameChanged;
	}

	public void setMemberUsernameChanged() {
		this.memberUsernameChanged = true;
	}

	public String getConnectedMemberUsername() {
		return this.loggedInUsername;
	}
	
	public void setMemberUsername(String username) {
		this.loggedInUsername = username;
	}
}
