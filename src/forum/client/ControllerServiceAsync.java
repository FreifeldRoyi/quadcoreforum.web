package forum.client;

import java.awt.Component;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

import forum.shared.ConnectedUserData;
import forum.shared.MessageModel;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.ActiveConnectedData;
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

	void getReplies(long threadID, MessageModel loadConfig, boolean shouldUpdateViews,
			AsyncCallback<List<MessageModel>> tNewCallback);

	void getMessageByID(long id, AsyncCallback<MessageModel> asyncCallback);

	void addNewSubject(long userID, long fatherID, String name,
			String description, AsyncCallback<SubjectModel> callback);

	void addReplyToMessage(long author, long replyTo, String title,
			String content, AsyncCallback<MessageModel> callback);

	void addNewThread(long userID, long subjectID, String topic, String title,
			String content, AsyncCallback<ThreadModel> callback);

	void deleteSubject(long userID, long fatherID, long subjectID,
			AsyncCallback<Void> callback);

	void getActiveUsersNumber(AsyncCallback<ActiveConnectedData> callback);

	void deleteMessage(long userID, long fatherID, long messageID,
			AsyncCallback<Void> callback);

	void modifyMessage(long authorID, long messageID, String newTitle,
			String newContent, AsyncCallback<Void> callback);

	void modifySubject(long authorID, long subjectID, String newName,
			String newDescription, AsyncCallback<Void> callback);

	void login(long guestID, String username, String password,
			AsyncCallback<ConnectedUserData> callback);

	void modifyThread(long authorID, long threadID, String newTopic,
			AsyncCallback<Void> callback);

	void logout(String username, AsyncCallback<ConnectedUserData> callback);
	
	void searchByAuthor(String username, AsyncCallback<List<Object>> callback);
	
}