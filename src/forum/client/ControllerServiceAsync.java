package forum.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

public interface ControllerServiceAsync {
	void registerToForum(RegisterMessage data, AsyncCallback<ServerResponse> callback);	
}