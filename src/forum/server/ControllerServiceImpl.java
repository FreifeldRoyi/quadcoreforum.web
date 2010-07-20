package forum.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import forum.client.ControllerService;
import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.MainForumLogic;
import forum.server.domainlayer.interfaces.UIUser;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotConnectedException;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

@SuppressWarnings("serial")
public class ControllerServiceImpl extends RemoteServiceServlet implements
ControllerService {

	private Collection<Long> connectedGuests;
	private Map<Long, String> connectedMembers;

	private ForumFacade facade;
	private MessagesController messagesController;
	
	@Override
	public void destroy() {
		for (long guestID : this.connectedGuests)
			this.disconnectClient(guestID);
		for (long userID : connectedMembers.keySet())
			this.disconnectClient(userID);
		super.destroy();
	}

	public ControllerServiceImpl(){
		try {
			if (facade == null)
				facade = MainForumLogic.getInstance();
			connectedGuests = new HashSet<Long>();
			connectedMembers = new HashMap<Long, String>();
			this.messagesController = new MessagesController(facade);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called when a browser window is closed and therefore the connected client (either guest or logged in)
	 * should be disconnected from the server
	 * 
	 * @param clientID
	 * 		The id of the client which should be disconnected from the server
	 */
	public void disconnectClient(long clientID) {
		if (clientID < 0) {
			this.connectedGuests.remove(clientID);
			this.facade.removeGuest(clientID);
		}
		else {
			String tUsername = this.connectedMembers.get(clientID);
			if (tUsername != null)
				try {
					this.facade.logout(tUsername);
				}
			catch (NotConnectedException e) {}
		}
	}


	public ServerResponse addNewGuest() throws forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			ServerResponse returnObj = new ServerResponse("", true); 
			UIUser tNewGuest = facade.addGuest();
			returnObj.setGuestIDChanged();
			returnObj.setConnectedGuestID(tNewGuest.getID());
			connectedGuests.add(tNewGuest.getID());
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tNewGuest.getID() + "");
			return returnObj;
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	public ServerResponse registerToForum(RegisterMessage data) {
		ServerResponse returnObj = new ServerResponse("", true); 
		try {
			facade.registerNewMember(data.getUsername(), data.getPassword(), 
					data.getLastName(), data.getFirstName(), data.getEmail());
			returnObj.setHasExecuted(true);
			returnObj.setResponse("registersuccess\t" + "you successfuly registered the forum");
		}
		catch (MemberAlreadyExistsException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("registererror\t" + "The following data already exists: " + e.getMessage());
		} 
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("registererror\t" + e.getMessage());
		}
		return returnObj;
	}

	@Override
	public SubjectModel getSubjectByID(long subjectID)
			throws forum.shared.exceptions.message.SubjectNotFoundException,
			forum.shared.exceptions.database.DatabaseRetrievalException {
		return messagesController.getSubjectByID(subjectID);
	}

	@Override
	public List<SubjectModel> getSubjects(SubjectModel father)
			throws forum.shared.exceptions.message.SubjectNotFoundException,
			forum.shared.exceptions.database.DatabaseRetrievalException {
		return messagesController.getSubjects(father);
	}

	@Override
	public PagingLoadResult<ThreadModel> getThreads(
			PagingLoadConfig loadConfig, long fatherID)
			throws forum.shared.exceptions.message.SubjectNotFoundException,
			forum.shared.exceptions.database.DatabaseRetrievalException {
		return messagesController.getThreads(loadConfig, fatherID);
	}
}
