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
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectAlreadyExistsException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.ThreadNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotConnectedException;
import forum.shared.ConnectedUserData;
import forum.shared.MessageModel;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.exceptions.database.DatabaseRetrievalException;
import forum.shared.exceptions.message.MessageNotFoundException;
import forum.shared.exceptions.user.NotRegisteredException;
import forum.shared.exceptions.user.WrongPasswordException;
import forum.shared.ActiveConnectedData;
import forum.shared.tcpcommunicationlayer.RegisterMessage;
import forum.shared.tcpcommunicationlayer.ServerResponse;

@SuppressWarnings("serial")
public class ControllerServiceImpl extends RemoteServiceServlet implements
ControllerService {

	private Collection<Long> connectedGuests;
	private Map<Long, String> connectedMembers;

	private ForumFacade facade;
	private MessagesController messagesController;
	private UsersController usersController;


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
			this.usersController = new UsersController(facade);

		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}




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
	NotRegisteredException, WrongPasswordException, DatabaseRetrievalException {
		return this.usersController.login(guestID, username, password);
	}

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
			DatabaseRetrievalException {
		messagesController.deleteSubject(userID, fatherID, subjectID);
	}


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
			forum.shared.exceptions.database.DatabaseUpdateException {
		messagesController.modifyMessage(authorID, messageID, newTitle, newContent);
	}

	public void modifyThread(final long authorID, long threadID, 
			String newTopic) throws forum.shared.exceptions.message.ThreadNotFoundException,
			NotRegisteredException,
			forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException {
		messagesController.modifyThread(authorID, threadID, newTopic);
	}

	public void modifySubject(final long authorID, long subjectID, 
			String newName, String newDescription) throws
			NotRegisteredException,
			forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException,
			forum.shared.exceptions.message.SubjectAlreadyExistsException,
			forum.shared.exceptions.message.SubjectNotFoundException {
		messagesController.modifySubject(authorID, subjectID, newName, newDescription);
	}

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
			forum.shared.exceptions.database.DatabaseUpdateException {
		return messagesController.addReplyToMessage(author, replyTo, title, content);
	}

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
	forum.shared.exceptions.database.DatabaseUpdateException {
		messagesController.deleteMessage(userID, fatherID, messageID);
	}

	/**
	 * Adds a new message to the forum.
	 * @return 
	 * @throws forum.shared.exceptions.database.DatabaseUpdateException 
	 * @throws forum.shared.exceptions.user.NotPermittedException 
	 * @throws NotRegisteredException 
	 * @throws SubjectAlreadyExistsException 
	 * @throws forum.shared.exceptions.message.SubjectAlreadyExistsException 
	 * @throws forum.shared.exceptions.message.SubjectNotFoundException 
	 * @throws SubjectNotFoundException 
	 * @throws  
	 */
	public SubjectModel addNewSubject(final long userID, final long fatherID,
			final String name, final String description) throws NotRegisteredException, 
			forum.shared.exceptions.user.NotPermittedException,
			forum.shared.exceptions.database.DatabaseUpdateException,
			forum.shared.exceptions.message.SubjectAlreadyExistsException, 
			forum.shared.exceptions.message.SubjectNotFoundException {
		return messagesController.addNewSubject(userID, fatherID, name, description);
	}

	public ThreadModel addNewThread (final long userID, final long subjectID, final String topic, final String title,
			final String content) throws forum.shared.exceptions.message.SubjectNotFoundException, 
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException,
			forum.shared.exceptions.database.DatabaseUpdateException {
		return messagesController.addNewThread(userID, subjectID, topic, title, content);
	}
	
	public ActiveConnectedData getActiveUsersNumber() throws DatabaseRetrievalException {
		try {
		return new ActiveConnectedData(facade.getActiveGuestsNumber(), facade.getActiveMemberUserNames());
		}
		catch (forum.server.updatedpersistentlayer.DatabaseRetrievalException e) {
			throw new DatabaseRetrievalException();
		}
	}

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

	@Override
	public MessageModel getMessageByID(long messageID)
	throws MessageNotFoundException, DatabaseRetrievalException {
		return messagesController.getMessageByID(messageID);
	}

	@Override
	public List<MessageModel> getReplies(long threadID, MessageModel loadConfig, boolean shouldUpdateViews)
	throws MessageNotFoundException, DatabaseRetrievalException {
		return messagesController.getReplies(threadID, loadConfig, shouldUpdateViews);
	}
}
