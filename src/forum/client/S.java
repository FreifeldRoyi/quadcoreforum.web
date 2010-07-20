/*package forum.client;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import forum.client.ControllerService;
import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.MainForumLogic;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.domainlayer.interfaces.UIUser;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;
import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;
import forum.shared.tcpcommunicationlayer.ViewSubjectsMessage;

@SuppressWarnings("serial")
public class S extends RemoteServiceServlet implements
ControllerService {
	
	private ForumFacade facade;

	public ControllerServiceImpl(){
		try {
			if (facade == null)
				facade = MainForumLogic.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ServerResponse addNewGuest() {
		ServerResponse returnObj = new ServerResponse("", true); 
		UIUser tNewGuest = facade.addGuest();
		returnObj.setGuestIDChanged();
		returnObj.setConnectedGuestID(tNewGuest.getID());
		returnObj.setHasExecuted(true);
		returnObj.setResponse(tNewGuest.getID() + "");
		return returnObj;
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
	public ServerResponse getSubjects(ViewSubjectsMessage data) {
		//TODO - I consider failure only in the case of an exception. Is it o.k???
		// Response (Vitali) --> Yes!!!

		ServerResponse returnObj = new ServerResponse("", true); 
		try {
			Collection<UISubject> tRetrievedSubjects = facade.getSubjects(data.getFatherID());
			// return a String representation of the retrieved subjects
			String tResponse = "";
			if (tRetrievedSubjects.isEmpty())
				tResponse = "There are no subjects under the root subject with id " + data.getFatherID() + " to view";
			else {
				Iterator<UISubject> iter = tRetrievedSubjects.iterator();
				while(iter.hasNext())
					tResponse += iter.next().toString() + "\n\t\r";
			}
			System.out.println(tResponse + " ddddddddddddddddddddddd");
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tResponse);
		}
		catch (SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		return returnObj;
	}
}
*/