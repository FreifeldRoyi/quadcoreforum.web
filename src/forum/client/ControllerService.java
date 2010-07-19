package forum.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;
import forum.shared.tcpcommunicationlayer.ViewSubjectsMessage;


@RemoteServiceRelativePath("controller")
public interface ControllerService extends RemoteService {
	
	ServerResponse registerToForum(RegisterMessage data);
	ServerResponse getSubjects(ViewSubjectsMessage data);	

}
