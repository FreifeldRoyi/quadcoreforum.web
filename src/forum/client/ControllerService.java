package forum.client;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import forum.shared.MessageModel;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.exceptions.message.MessageNotFoundException;
import forum.shared.exceptions.message.SubjectNotFoundException;
import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

@RemoteServiceRelativePath("controller")
public interface ControllerService extends RemoteService {

	ServerResponse addNewGuest() throws DatabaseUpdateException;
	void disconnectClient(long clientID);

	ServerResponse registerToForum(RegisterMessage data);
	List<SubjectModel> getSubjects(SubjectModel father) 
	throws SubjectNotFoundException, DatabaseRetrievalException;

	SubjectModel getSubjectByID(long subjectID) throws SubjectNotFoundException, 
	DatabaseRetrievalException;
	
	PagingLoadResult<ThreadModel> getThreads(PagingLoadConfig loadConfig, long fatherID) throws SubjectNotFoundException,
		DatabaseRetrievalException ;

	List<MessageModel> getReplies(long threadID, MessageModel 
			loadConfig, boolean shouldUpdateViews) throws MessageNotFoundException, DatabaseRetrievalException;

	MessageModel getMessageByID(long
			id) throws MessageNotFoundException, DatabaseRetrievalException;

}
