package forum.client;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import forum.shared.ConnectedUserData;
import forum.shared.MessageModel;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.database.DatabaseUpdateException;
import forum.shared.exceptions.message.MessageNotFoundException;
import forum.shared.exceptions.message.SubjectNotFoundException;
import forum.shared.exceptions.message.ThreadNotFoundException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;
import forum.shared.ActiveConnectedData;
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


	/*	public ServerResponse getMemberDetails(final long memberID) {

	}

		public abstract void recoverPassword(final String username, final String email, final String password, final Component comp);

		public abstract void changePassword(final long memberID, final String prevPassword, final String newPassword, 
				final boolean shouldAskNewPassword, final Component comp);

		public abstract void updateMemberDetails(final Component comp, final long memberID, final String username,
				final String firstName, final String lastName, final String email);

		public abstract void registerToForum(final Component comp, String username, String password, 
				String email, String firstName, String lastName);
	 */	


	public ConnectedUserData login(long guestID, String username, String password) throws 
	NotRegisteredException, WrongPasswordException, DatabaseRetrievalException;
	
	public ConnectedUserData logout(String username) throws 
	forum.shared.exceptions.user.NotConnectedException,
	forum.shared.exceptions.database.DatabaseUpdateException;

	
	/**
	 * Deletes recursively the subject with the given id and all its content.
	 * 
	 * @param id The id of the subject to delete.
	 * @throws forum.shared.exceptions.message.SubjectNotFoundException 
	 * @throws NotRegisteredException 
	 * @throws forum.shared.exceptions.user.NotPermittedException 
	 * @throws DatabaseRetrievalException 
	 */
	public void deleteSubject(long userID, final long fatherID, 
			long subjectID) throws forum.shared.exceptions.message.SubjectNotFoundException,
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException,
			DatabaseRetrievalException;


	/**
	 * Tries to modify a message.
	 * 
	 * @param id The id of the message to be modified.
	 * @param newContent The new content of the message.
	 * @throws forum.shared.exceptions.database.DatabaseUpdateException 
	 * @throws forum.shared.exceptions.user.NotPermittedException 
	 * @throws NotRegisteredException 
	 * @throws MessageNotFoundException 
	 */
	public void modifyMessage(final long authorID, long messageID, String newTitle,
			String newContent) throws MessageNotFoundException,
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException;

	public void modifyThread(final long authorID, long threadID, 
			String newTopic) throws ThreadNotFoundException, NotRegisteredException,
			forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException;

	public void modifySubject(final long authorID, long subjectID, 
			String newName, String newDescription) throws SubjectNotFoundException,
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException, 
			forum.shared.exceptions.message.SubjectAlreadyExistsException;

	/**
	 * Adds a reply message.
	 * @throws forum.shared.exceptions.database.DatabaseUpdateException 
	 * @throws forum.shared.exceptions.user.NotPermittedException 
	 * @throws NotRegisteredException 
	 * @throws MessageNotFoundException 
	 * */
	public MessageModel addReplyToMessage(final long author, final long replyTo, 
			final String title, final String content) throws MessageNotFoundException, 
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException;

	/**
	 * Deletes recursively the message id and all its sons.
	 * 
	 * @param id The id of the message to delete.
	 * @throws forum.shared.exceptions.database.DatabaseUpdateException 
	 * @throws forum.shared.exceptions.user.NotPermittedException 
	 * @throws NotRegisteredException 
	 * @throws MessageNotFoundException 
	 */
	public void deleteMessage(long userID, long fatherID, long messageID) throws
	MessageNotFoundException, NotRegisteredException, 
	forum.shared.exceptions.user.NotPermittedException, 
	forum.shared.exceptions.database.DatabaseUpdateException;

	/**
	 * Adds a new message to the forum.
	 * @return 
	 * @throws forum.shared.exceptions.database.DatabaseUpdateException 
	 * @throws forum.shared.exceptions.user.NotPermittedException 
	 * @throws NotRegisteredException 
	 * @throws SubjectAlreadyExistsException 
	 * @throws SubjectNotFoundException 
	 */
	public SubjectModel addNewSubject(final long userID, final long fatherID,
			final String name, final String description) throws SubjectNotFoundException,
			forum.shared.exceptions.message.SubjectAlreadyExistsException, NotRegisteredException, 
			forum.shared.exceptions.user.NotPermittedException,
			forum.shared.exceptions.database.DatabaseUpdateException;

	public ThreadModel addNewThread (final long userID, final long subjectID, 
			final String topic, final String title, final String content) throws 
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException,
			forum.shared.exceptions.database.DatabaseUpdateException, 
			forum.shared.exceptions.message.SubjectNotFoundException;
	
	public ActiveConnectedData getActiveUsersNumber() throws DatabaseRetrievalException;
	
	/*		public abstract void searchByAuthor(Component comp, String username);

		public abstract void searchByContent(Component comp, String phrase);	

		public abstract void getAllMembers(Component comp);

		public abstract void promoteToModerator(Component comp, String username);

		public abstract void demoteToMember(Component comp, String username);


		// according to the prev message id we know if the number of view of the thread should be updated - 
		// if the ids are the same then no otherwise yes
		public abstract void getPath(Component comp, long prevFatherMessageID, long messageID);
	}






	 */

	List<Object> searchByAuthor(String username);
}
