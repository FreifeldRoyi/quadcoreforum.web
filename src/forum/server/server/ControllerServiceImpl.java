package forum.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import forum.client.ControllerService;
import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.MainForumLogic;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;
import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;
import forum.shared.tcpcommunicationlayer.ViewSubjectsMessage;

@SuppressWarnings("serial")
public class ControllerServiceImpl extends RemoteServiceServlet implements
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
		// TODO Auto-generated method stub
		return null;
	}
}
