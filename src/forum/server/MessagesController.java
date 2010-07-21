/**
 * 
 */
package forum.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.domainlayer.interfaces.UIThread;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.shared.MessageModel;
import forum.shared.SubjectModel;
import forum.shared.ThreadModel;
import forum.shared.exceptions.message.MessageNotFoundException;

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
				System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
				return toReturn;
			}
			else {
				Iterator<UISubject> iter = tRetrievedSubjects.iterator();
				while (iter.hasNext()) {
					UISubject tCurrent = iter.next();
					System.out.println(tCurrent.toString());
					toReturn.add(new SubjectModel(tCurrent.getID(), 
							tCurrent.getName(), tCurrent.getDescription(),
							tCurrent.getDeepNumOfSubSubjects(),
							tCurrent.getDeepNumOfMessages()));
				}
				System.out.println("cccccccc");
				return toReturn;
			}
		}
		catch (SubjectNotFoundException e) {
			System.out.println("ddddddddddddddddddddddddd");
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (DatabaseRetrievalException e) {
			System.out.println("ddddddddddddddddddddddddd");
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}

	public SubjectModel getSubjectByID(long subjectID) throws forum.shared.exceptions.message.SubjectNotFoundException, 
	forum.shared.exceptions.database.DatabaseRetrievalException {
		try {
			UISubject tFather = facade.getSubjectByID(subjectID);
			return new SubjectModel(tFather.getID(), tFather.getName(), tFather.getDescription(),
					tFather.getDeepNumOfSubSubjects(), tFather.getDeepNumOfMessages());
		}
		catch (SubjectNotFoundException e) {
			throw new forum.shared.exceptions.message.SubjectNotFoundException(e.getID());
		}
		catch (DatabaseRetrievalException e) {
			System.out.println("ddddddddddddddddddddddddd");
			throw new forum.shared.exceptions.database.DatabaseRetrievalException();
		}
	}

	public PagingLoadResult<ThreadModel> getThreads(
			PagingLoadConfig loadConfig, long fatherID)
			throws forum.shared.exceptions.message.SubjectNotFoundException,
			forum.shared.exceptions.database.DatabaseRetrievalException {







		System.out.println(loadConfig == null);
		if (loadConfig != null) {
			System.out.println(loadConfig.getOffset());
			System.out.println(loadConfig.getLimit());
		}

		try {
			Collection<UIThread> tThreads = facade.getThreads(fatherID);
			List<ThreadModel> tData = new ArrayList<ThreadModel>();


			int tStart = loadConfig.getOffset();  
			int limit = tThreads.size();  
			if (loadConfig.getLimit() > 0)
				limit = Math.min(tStart + loadConfig.getLimit(), limit);  

			List<UIThread> tThreadsAsList = new ArrayList<UIThread>(tThreads);

			for (int i = loadConfig.getOffset(); i < limit; i++) {
				UIThread tCurrentThread = tThreadsAsList.get(i);
				tData.add(new ThreadModel(tCurrentThread.getID(), 
						tCurrentThread.getTopic(), tCurrentThread.getNumOfResponses(), tCurrentThread.getNumOfViews()));
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
		catch (Exception e) {}
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

	private MessageModel messageToMessageModelConvertor(UIMessage message) {
		return new MessageModel(message.getMessageID(), message.getAuthorID(), this.getAuthorUsername(message),
				message.getTitle(), message.getContent(), message.getDateTime());		
	}

}
