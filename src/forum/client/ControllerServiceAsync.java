package forum.client;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

public interface ControllerServiceAsync {
	void addNewGuest(AsyncCallback<ServerResponse> callback);	

	void disconnectClient(long clientID, AsyncCallback<Void> callback);

	void registerToForum(RegisterMessage data, AsyncCallback<ServerResponse> callback);	
	void getSubjects(SubjectModel father, AsyncCallback<List<SubjectModel>> callback);

	void getSubjectByID(long sujectID, AsyncCallback<SubjectModel> callback);
	
	void getThreads(PagingLoadConfig loadConfig, long fatherID, 
			AsyncCallback<PagingLoadResult<ThreadModel>> callback);

	
}