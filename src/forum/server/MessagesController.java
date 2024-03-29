/**
 * 
 */
package forum.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.domainlayer.interfaces.UIThread;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.domainlayer.search.SearchHit;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.ThreadNotFoundException;
import forum.shared.MessageModel;
import forum.shared.Permission;
import forum.shared.SearchHitModel;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.exceptions.message.MessageNotFoundException;
import forum.shared.exceptions.user.NotRegisteredException;

/**
 * @author sepetnit
 *
 */
public class MessagesController {
	private ForumFacade facade;

	public MessagesController(ForumFacade facade) {
		this.facade = facade;
	}

	public List<SubjectModel> getSubjects(SubjectModel father) throws forum.shared.exceptions.message.SubjectNotFoundException,
	forum.shared.exceptions.database.DatabaseRetrievalException {
		try {
			List<SubjectModel> toReturn = new ArrayList<SubjectModel>();
			Collection<UISubject> tRetrievedSubjects = facade.getSubjects(father == null? -1 : 
				father.getID());
			if (tRetrievedSubjects.isEmpty()) {
				return toReturn;
			}
			else {
				Iterator<UISubject> iter = tRetrievedSubjects.iterator();
				while (iter.hasNext()) {
					toReturn.add(subjectToSubjectModelConvertor(iter.next()));
				}
				return toReturn;
			}
		}
		catch (SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (DatabaseRetrievalException e) {
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}

	public SubjectModel getSubjectByID(long subjectID) throws forum.shared.exceptions.message.SubjectNotFoundException, 
	forum.shared.exceptions.database.DatabaseRetrievalException {
		try {
			return subjectToSubjectModelConvertor(facade.getSubjectByID(subjectID));
		}
		catch (SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (DatabaseRetrievalException e) {
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}

	public ThreadModel getThreadByID(long threadID, boolean shouldUpdateViews) throws forum.shared.exceptions.message.ThreadNotFoundException, 
	forum.shared.exceptions.database.DatabaseRetrievalException {
		try {
			return threadToThreadModelConvertor(facade.getThreadByID(threadID, shouldUpdateViews));
		}
		catch (ThreadNotFoundException e) {
			throw new forum.shared.exceptions.message.ThreadNotFoundException(e.getThreadID());
		}
		catch (DatabaseRetrievalException e) {
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}
	
	public void deleteSubject(long userID, final long fatherID, 
			long subjectID) throws forum.shared.exceptions.message.SubjectNotFoundException,
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException,
			forum.shared.exceptions.database.DatabaseRetrievalException {
		try {
			facade.deleteASubject(userID, fatherID, subjectID);
		}
		catch (SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException();
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}

	public MessageModel modifyMessage(final long authorID, long messageID, String newTitle,
			String newContent) throws MessageNotFoundException, NotRegisteredException,
			forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			return messageToMessageModelConvertor(facade.updateAMessage(authorID, messageID, newTitle, newContent));
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException e) {
			throw new MessageNotFoundException(e.getID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException(e.getUserID(),
					Permission.valueOf(e.getPermission().toString()));
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	public ThreadModel modifyThread(final long authorID, long threadID, 
			String newTopic) throws NotRegisteredException, 
			forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException,
			forum.shared.exceptions.message.ThreadNotFoundException {
		try {
			return threadToThreadModelConvertor(facade.updateAThread(authorID, threadID, newTopic));
		}
		catch (ThreadNotFoundException e) {
			throw new forum.shared.exceptions.message.ThreadNotFoundException(e.getThreadID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException(e.getUserID(),
					Permission.valueOf(e.getPermission().toString()));
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}		
	}

	public SubjectModel modifySubject(final long authorID, long subjectID, 
			String newName, String newDescription) throws 
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException,
			forum.shared.exceptions.database.DatabaseUpdateException, 
			forum.shared.exceptions.message.SubjectAlreadyExistsException,
			forum.shared.exceptions.message.SubjectNotFoundException {
		
		try {
			return subjectToSubjectModelConvertor(facade.updateASubject(authorID, subjectID, newName, newDescription));
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectAlreadyExistsException e) {
			forum.shared.exceptions.message.SubjectAlreadyExistsException tExToThrow =
				new forum.shared.exceptions.message.SubjectAlreadyExistsException(e.getSubjectID());
			tExToThrow.setSubjectName(e.getSubjectName());
			throw tExToThrow;
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException(e.getUserID(),
					Permission.valueOf(e.getPermission().toString()));
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}		
	}

	public MessageModel addReplyToMessage(final long author, final long replyTo, 
			final String title, final String content) throws MessageNotFoundException,
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException,
			forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			return messageToMessageModelConvertor(facade.addNewReply(author, replyTo, title, content));
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException e) {
			throw new MessageNotFoundException(e.getID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException(e.getUserID(), 
					Permission.valueOf(e.getPermission().toString()));
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	public void deleteMessage(long userID, long fatherID, long messageID) throws MessageNotFoundException,
	NotRegisteredException, forum.shared.exceptions.user.NotPermittedException,
	forum.shared.exceptions.database.DatabaseUpdateException {
		try {
			facade.deleteAMessage(userID, fatherID, messageID);
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException e) {
			throw new MessageNotFoundException(e.getID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException(e.getUserID(), 
					Permission.valueOf(e.getPermission().toString()));
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}
	}

	public SubjectModel addNewSubject(final long userID, final long fatherID,
			final String name, final String description) throws
			NotRegisteredException,
			forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException,
			forum.shared.exceptions.message.SubjectAlreadyExistsException,
			forum.shared.exceptions.message.SubjectNotFoundException {
		try {
			return subjectToSubjectModelConvertor(facade.addNewSubject(userID, fatherID, name, description));
		} 
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectAlreadyExistsException e) {
			forum.shared.exceptions.message.SubjectAlreadyExistsException tExToThrow =
				new forum.shared.exceptions.message.SubjectAlreadyExistsException(e.getSubjectID());
			tExToThrow.setSubjectName(e.getSubjectName());
			throw tExToThrow;
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException(e.getUserID(),
					Permission.valueOf(e.getPermission().toString()));
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}		
	}

	public ThreadModel addNewThread (final long userID, final long subjectID, 
			final String topic, final String title, final String content) throws
			NotRegisteredException, forum.shared.exceptions.user.NotPermittedException, 
			forum.shared.exceptions.database.DatabaseUpdateException,
			forum.shared.exceptions.message.SubjectNotFoundException {
		try {
			return threadToThreadModelConvertor(facade.openNewThread(userID, subjectID, topic, title, content));
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) {
			throw new NotRegisteredException(e.getUserID());
		}
		catch (NotPermittedException e) {
			throw new forum.shared.exceptions.user.NotPermittedException(e.getUserID(),
					Permission.valueOf(e.getPermission().toString()));
		}
		catch (DatabaseUpdateException e) {
			throw new forum.shared.exceptions.database.DatabaseUpdateException();
		}		
	}

	public PagingLoadResult<ThreadModel> getThreads(
			PagingLoadConfig loadConfig, long fatherID)
			throws forum.shared.exceptions.message.SubjectNotFoundException,
			forum.shared.exceptions.database.DatabaseRetrievalException {


		try {
			Collection<UIThread> tThreads = facade.getThreads(fatherID);
			List<ThreadModel> tData = new ArrayList<ThreadModel>();

			int tStart = loadConfig.getOffset();  
			int limit = tThreads.size();  
			if (loadConfig.getLimit() > 0)
				limit = Math.min(tStart + loadConfig.getLimit(), limit);  

			List<UIThread> tThreadsAsList = new ArrayList<UIThread>(tThreads);

			for (int i = loadConfig.getOffset(); i < limit; i++) {
				tData.add(threadToThreadModelConvertor(tThreadsAsList.get(i)));
			}
			return new BasePagingLoadResult<ThreadModel>(tData, loadConfig.getOffset(), tThreads.size());
		} 
		catch (SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (DatabaseRetrievalException e) {
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}

	public MessageModel getMessageByID(long messageID) throws MessageNotFoundException,
	forum.shared.exceptions.database.DatabaseRetrievalException{
		try {
			return this.messageToMessageModelConvertor(facade.getMessageByID(messageID));
		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException e) {
			throw new MessageNotFoundException(e.getID());
		}
		catch (DatabaseRetrievalException e) {
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}

	/**
	 * Finds and returns the user-name of the given message author
	 * 
	 * @param forum
	 * 		An instance of the ForumFacade from which the data should be retrieved
	 * @param message
	 * 		The message whose author user-name should be retrieved
	 * @return
	 * 		The user-name of the message author
	 */
	private String getAuthorUsername(UIMessage message) {
		String toReturn = "<Author-Not-Found>";
		try {
			toReturn = facade.getMemberByID(message.getAuthorID()).getUsername();
		}
		catch (Exception e) {
			// do nothing - return a default user-name
		}
		return toReturn;		
	}

	public List<MessageModel> getReplies(long threadID, MessageModel father, boolean shouldUpdateViews) throws MessageNotFoundException,
	forum.shared.exceptions.database.DatabaseRetrievalException {
		try {
			List<MessageModel> toReturn = new ArrayList<MessageModel>();
			if (father == null)
				toReturn.add(this.getMessageByID(threadID));
			else {
				Collection<UIMessage> tRetrievedMessages = facade.getReplies(father.getID(), shouldUpdateViews);
				if (tRetrievedMessages.isEmpty()) {
					return toReturn;
				}
				else {
					Iterator<UIMessage> iter = tRetrievedMessages.iterator();
					while (iter.hasNext()) {
						toReturn.add(messageToMessageModelConvertor(iter.next()));			
					}
				}
			}
			return toReturn;

		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException e) {
			throw new MessageNotFoundException(e.getID());
		}
		catch (DatabaseRetrievalException e) {
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}
	
	public PagingLoadResult<SearchHitModel> search(PagingLoadConfig loadConfig, 
			String type, String searchPhrase) 
			throws MessageNotFoundException, 
			forum.shared.exceptions.database.DatabaseRetrievalException, 
			forum.shared.exceptions.message.ThreadNotFoundException, 
			forum.shared.exceptions.message.SubjectNotFoundException, 
			NotRegisteredException
	{
		SearchHit[] rawHits = null;
		if (type.equals("author"))
		{
			try 
			{
				rawHits = this.facade.searchByAuthor(this.facade.getMemberIdByUsernameAndOrEmail(searchPhrase, null),
						0, Integer.MAX_VALUE);
			} 
			catch (forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException e) 
			{
				throw new NotRegisteredException(e.getUserID());
			}
			catch (DatabaseRetrievalException e) 
			{
				throw new forum.shared.exceptions.database.DatabaseRetrievalException();
			}
		}
		else if (type.equals("content"))
		{
			rawHits = this.facade.searchByContent(searchPhrase, 0, Integer.MAX_VALUE);
		}
		else
		{
			//TODO add new exception
		}
		
		int tStart = loadConfig.getOffset();
		int limit = 0;
		List<SearchHitModel> tSearchData = this.searchReturn(rawHits);
		limit = tSearchData.size();
		if (loadConfig.getLimit() > 0)
			limit = Math.min(tStart + loadConfig.getLimit(), limit);  
		
		List<SearchHitModel> tSearchHitsToReturn = new ArrayList<SearchHitModel>();

		for (int i = loadConfig.getOffset(); i < limit; i++) {
			tSearchHitsToReturn.add(tSearchData.get(i));
		}
		
		
		return new BasePagingLoadResult<SearchHitModel>(tSearchHitsToReturn, loadConfig.getOffset(), tSearchData.size());
	}
	
	private List<SearchHitModel> searchReturn(SearchHit[] rawHits) 
	throws MessageNotFoundException, 
	forum.shared.exceptions.database.DatabaseRetrievalException,
	forum.shared.exceptions.message.ThreadNotFoundException, 
	forum.shared.exceptions.message.SubjectNotFoundException
	{
		List<SearchHitModel> toReturn = new ArrayList<SearchHitModel>();
			
		if (rawHits != null)
		{
			for (SearchHit hit : rawHits)
			{
				toReturn.add(SearchHitToModelConvertor(hit));
			}
		}	
		
		return toReturn;
	}
	
	private SearchHitModel SearchHitToModelConvertor(SearchHit hit) 
	throws MessageNotFoundException, 
	forum.shared.exceptions.database.DatabaseRetrievalException, 
	forum.shared.exceptions.message.ThreadNotFoundException, forum.shared.exceptions.message.SubjectNotFoundException
	{

		try
		{
		UIMessage tMsg = facade.getMessageByID(hit.getMessage().getMessageID());
		long tMsgID = tMsg.getMessageID();
		String tTitle = tMsg.getTitle();
		String tAuthor = this.getAuthorUsername(tMsg);
		String tContent = tMsg.getContent();
		
		double tScore = hit.getScore();
		
		Stack<MessageModel> tMsgPath = new Stack<MessageModel>();
		
		ThreadModel tContainingThread;
		Stack<SubjectModel> tSubjPath = new Stack<SubjectModel>();
		
		tMsgPath.push(this.messageToMessageModelConvertor(tMsg));

		
			while (tMsg.getFatherID() != -1)
			{
				tMsg = this.facade.getMessageByID(tMsg.getFatherID());
				tMsgPath.push(this.messageToMessageModelConvertor(tMsg));
			}
			
			UIThread tRawThread = this.facade.getThreadByID(tMsg.getMessageID(), false);
			tContainingThread = this.threadToThreadModelConvertor(tRawThread);
			
			UISubject tRawSubject = this.facade.getSubjectByID(tRawThread.getFatherID());
			tSubjPath.push(this.subjectToSubjectModelConvertor(tRawSubject));
			while (tRawSubject.getFatherID() != -1)
			{
				tRawSubject = this.facade.getSubjectByID(tRawSubject.getFatherID());
				tSubjPath.push(this.subjectToSubjectModelConvertor(tRawSubject));
			}
			
			return new SearchHitModel(tMsgID, tMsgPath, tContainingThread, tSubjPath,
					tTitle, tAuthor, tContent, tScore);

		}
		catch (forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException e)
		{
			throw new MessageNotFoundException();
		}
		catch (DatabaseRetrievalException e)
		{
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
		catch (ThreadNotFoundException e)
		{
			throw new forum.shared.exceptions.message.ThreadNotFoundException(e.getThreadID());
		}
		catch (SubjectNotFoundException e)
		{
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		
	}

	private MessageModel messageToMessageModelConvertor(UIMessage message) {
		return new MessageModel(message.getMessageID(), message.getAuthorID(), this.getAuthorUsername(message),
				message.getTitle(), message.getContent(), message.getDateTime());		
	}

	private SubjectModel subjectToSubjectModelConvertor(UISubject subject) {
		return new SubjectModel(subject.getID(), subject.getName(),
				subject.getDescription(), subject.getDeepNumOfSubSubjects(),
				subject.getDeepNumOfMessages());
	}
	
	private ThreadModel threadToThreadModelConvertor(UIThread thread) {
		return new ThreadModel(thread.getID(), thread.getTopic(), 
				thread.getNumOfResponses(), thread.getNumOfViews());
	}
}
