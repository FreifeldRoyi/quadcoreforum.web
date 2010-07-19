package forum.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;
import forum.shared.tcpcommunicationlayer.ViewSubjectsMessage;

public interface ControllerServiceAsync {
	void registerToForum(RegisterMessage data, AsyncCallback<ServerResponse> callback);	
	void getSubjects(ViewSubjectsMessage data, AsyncCallback<ServerResponse> callback);	
}