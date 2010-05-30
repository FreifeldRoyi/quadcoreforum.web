/** 
 * This class is responsible of handling all the operations related to the forum content (- subjects, threads and messages)
 * which requires a communication to the persistent layer
 * 
 * In addition this class serves as a cache memory which holds a repository of loaded content of the forum database in order
 * to speed up the data access operations.
 */
package forum.server.domainlayer.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.*;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;

public class MessagesCache {	
	private final PersistenceDataHandler pipe; // A pipe to the persistence layer

	private long nextFreeSubjectID;
	private long nextFreeThreadID;
	private long nextFreeMessageID;

	private final Map<Long, ForumSubject> idsToSubjectsMapping;
	private final Map<Long, ForumThread> idsToThreadsMapping;
	private final Map<Long, ForumMessage> idsToMessagesMapping;

	/**
	 * The class constructor.
	 * 
	 * Initializes a new cache instance that handles all the forum content operations against the persistent layer
	 */
	public MessagesCache() throws DatabaseRetrievalException {
		this.pipe = PersistenceFactory.getPipe();
		// loads the next free forum content ids according to the forum database
		this.nextFreeSubjectID =  this.pipe.getFirstFreeSubjectID();
		this.nextFreeThreadID =  this.pipe.getFirstFreeThreadID();
		this.nextFreeMessageID = this.pipe.getFirstFreeMessageID();
		this.idsToSubjectsMapping = new HashMap<Long, ForumSubject>();
		this.idsToThreadsMapping = new HashMap<Long, ForumThread>();
		this.idsToMessagesMapping = new HashMap<Long, ForumMessage>();
	}

	/**
	 * 
	 * @return
	 * 		The next id which can be assigned to a new subject
	 * 
	 * 		The methods promises that the returned id hasn't been assigned yet to any subject
	 */
	private long getNextSubjectID() {
		return this.nextFreeSubjectID++;
	}

	/**
	 * 
	 * @return
	 * 		The next id which can be assigned to a new thread
	 * 
	 * 		The methods promises that the returned id hasn't been assigned yet to any thread
	 */
	private long getNextThreadID() {
		return this.nextFreeThreadID++;
	}

	/**
	 * 
	 * @return
	 * 		The next id which can be assigned to a new message
	 * 
	 * 		The methods promises that the returned id hasn't been assigned yet to any message
	 */
	private long getNextMessageID() {
		return this.nextFreeMessageID++;
	}

	// Subject related methods

	// TODO: allow top level subjects cache saving

	/**
	 * 
	 * Finds and returns the top-level subjects of the forum
	 * 
	 * @return
	 * 		A collection of the top-level subjects of the forum
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the database due to a database connection error
	 */
	public Collection<ForumSubject> getTopLevelSubjects() throws DatabaseRetrievalException {
		Collection<ForumSubject> toReturn = this.pipe.getTopLevelSubjects();
		for (ForumSubject tCurrentSubject : toReturn)
			this.idsToSubjectsMapping.put(tCurrentSubject.getID(), tCurrentSubject);
		return toReturn;
	}

	/**
	 * 
	 * Finds and returns a subject whose id is equal to the give one
	 * 
	 * @param subjectID
	 * 		The id of the forum-subject which should be retrieved
	 * 
	 * @return
	 * 		A forum-subject whose id equals to the given one
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id hasn't been found in the cache and the database
	 * @throws DatabaseRetrievalException
	 */
	public ForumSubject getSubjectByID(final long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		if (this.idsToSubjectsMapping.containsKey(subjectID))
			return this.idsToSubjectsMapping.get(subjectID);
		else {
			ForumSubject toReturn = this.pipe.getSubjectByID(subjectID);
			this.idsToSubjectsMapping.put(toReturn.getID(), toReturn);
			return toReturn;
		}
	}

	/**
	 * 
	 * Creates a new subject according to the given parameters and adds it to the forum database
	 * 
	 * @param name
	 * 		The name of the new subject
	 * @param description
	 * 		The description of the new subject
	 * @param isTopLevel
	 * 		Whether this subject should be added to the top-level of the forum
	 * 
	 * @return
	 * 		The created subject
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case the new subject can't be added to the database due to a database connection error
	 */
	public ForumSubject createNewSubject(final String name, final String description, final long fatherID) throws
	DatabaseUpdateException {
		long tSubjectID = this.getNextSubjectID();
		this.pipe.addNewSubject(tSubjectID, name, description, fatherID);
		ForumSubject tNewSubject = new ForumSubject(tSubjectID, name, description, fatherID);
		this.idsToSubjectsMapping.put(tNewSubject.getID(), tNewSubject);
		return tNewSubject;			
	}

	/**
	 * 
	 * Updates the data of the given forum subject in the forum database
	 * 
	 * @param updatedSubject
	 * 		The updated subject whose data should be updated in the database
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id hasn't been found in the database
	 * @throws DatabaseUpdateException
	 * 		In case the database can't be updated due to a database connection error
	 */
	public void updateInDatabase(ForumSubject updatedSubject) throws SubjectNotFoundException, DatabaseUpdateException {
		this.pipe.updateSubject(updatedSubject.getID(), updatedSubject.getSubSubjects(), updatedSubject.getThreads());
		if (!this.idsToSubjectsMapping.containsKey(updatedSubject.getID()))
			this.idsToSubjectsMapping.put(updatedSubject.getID(), updatedSubject);
	}

	// Thread related methods

	/**
	 * 
	 * Finds and returns a forum-thread whose id is equal to the given one
	 * 
	 * @param threadID
	 * 		The id of the thread which should be retrieved
	 * 
	 * @return
	 * 		The found forum-thread
	 * 
	 * @throws ThreadNotFoundException
	 * 		In case a thread with the given id hasn't been found neither in the cache nor in the database
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the database due to a database connection error
	 */
	public ForumThread getThreadByID(long threadID) throws ThreadNotFoundException, DatabaseRetrievalException {
		if (this.idsToThreadsMapping.containsKey(threadID))
			return this.idsToThreadsMapping.get(threadID);
		else {
			ForumThread toReturn = this.pipe.getThreadByID(threadID);
			this.idsToThreadsMapping.put(toReturn.getID(), toReturn);
			return toReturn;
		}
	}

	/**
	 * 
	 * Creates a new thread with the given parameters and adds it to the forum database
	 * 
	 * @param topic
	 * 		The new thread topic
	 * @param rootID
	 * 		The id of the message which should be the root message of the created thread
	 * @pre
	 * 		A message with the given id (rootID) exists in the database and doesn't associated with any thread
	 * 
	 * @return
	 * 		The created thread
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case the created thread can't be added to the database due to a database connection error
	 */
	public ForumThread openNewThread(final String topic, long rootID, final long fatherSubjectID) throws DatabaseUpdateException {
		//final long tThreadID = this.getNextThreadID();
		ForumThread tNewThread = new ForumThread(rootID, topic, rootID, fatherSubjectID);
		this.idsToThreadsMapping.put(rootID, tNewThread);
		this.pipe.openNewThread(rootID, topic, rootID, fatherSubjectID);
		return tNewThread;
	}

	/**
	 * 
	 * Deletes a thread with the given id from the forum database
	 * 
	 * @param threadID
	 * 		The id of the thread which should be deleted
	 * 
	 * @throws ThreadNotFoundException
	 * 		In case a thread with the given id wasn't found
	 * @throws DatabaseUpdateException
	 * 		In case the thread can't be deleted from the database due to a database connection error
	 */
	public void deleteATread(final long threadID) throws ThreadNotFoundException, DatabaseUpdateException {
		Collection<Long> tRecDeletedMessages = this.pipe.deleteAThread(threadID); // recursively deleted messages
		this.idsToThreadsMapping.remove(threadID);
		for (long tMessageID : tRecDeletedMessages)
			this.idsToMessagesMapping.remove(tMessageID);
	}

	public void updateInDatabase(ForumThread updatedThread) throws ThreadNotFoundException, DatabaseUpdateException {
		this.pipe.updateThread(updatedThread.getID(), updatedThread.getTopic());
		if (!this.idsToMessagesMapping.containsKey(updatedThread.getID()))
			this.idsToThreadsMapping.put(updatedThread.getID(), updatedThread);
	}
	
	// Message related methods

	/**
	 * 
	 * Finds and returns a message whose id equals to the given one
	 * 
	 * @param messageID
	 * 		The id of the message which should be returned
	 * @return
	 * 		The found message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id wasn't found neither in the cache memory nor in the database
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retreived from the database
	 */
	public ForumMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		if (this.idsToMessagesMapping.containsKey(messageID))
			return this.idsToMessagesMapping.get(messageID);
		else {
			ForumMessage toReturn = this.pipe.getMessageByID(messageID);
			this.idsToMessagesMapping.put(messageID, toReturn);
			return toReturn;
		}
	}
	
	/**
	 * 
	 * @return
	 * 		A collection of all the messages contained in the forum database
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the database due to a database connection error
	 */
	public Collection<ForumMessage> getAllMessages() throws DatabaseRetrievalException {
		return this.pipe.getAllMessages();
	}

	/**
	 * 
	 * Creates a new message according to the given parameters and adds it to the forum database
	 * 
	 * @param authorID
	 * 		The id of the message author
	 * @param title
	 * 		The title of the new message
	 * @param content
	 * 		The content of the new message
	 * 
	 * @return
	 * 		The created message
	 * @throws DatabaseUpdateException
	 * 		In case the created message can't be added to the database due to a database connection error
	 */
	public ForumMessage createNewMessage(final long authorID, final String title, 
			final String content, final long fatherID) throws DatabaseUpdateException {
		long tMessageID = this.getNextMessageID();
		ForumMessage tNewMessage = new ForumMessage(tMessageID, authorID, title, content, fatherID);
		this.pipe.addNewMessage(tMessageID, authorID, title, content, fatherID);
		this.idsToMessagesMapping.put(tMessageID, tNewMessage);
		return tNewMessage;
	}

	/**
	 * 
	 * Deletes a message with the given id from the forum database (and from the cache memory)
	 * 
	 * @param messageID
	 * 		The id of the message which should be deleted
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id hasn't been found in the database
	 * @throws DatabaseUpdateException
	 * 		In case the message can't be deleted from the database due to a database connection error
	 */
	public void deleteAMessage(long messageID) throws MessageNotFoundException, DatabaseUpdateException {
		Collection<Long> tRecDeletedMessages = this.pipe.deleteAMessage(messageID); // recursively deleted messages
		for (long tMessageID : tRecDeletedMessages)
			this.idsToMessagesMapping.remove(tMessageID);
	}

	/**
	 * 
	 * Updates the data of the given forum message in the forum database
	 * 
	 * @param updatedMessage
	 * 		The updated message whose data should be updated in the database
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id hasn't been found in the database
	 * @throws DatabaseUpdateException
	 * 		In case the database can't be updated due to a database connection error
	 */
	public void updateInDatabase(ForumMessage updatedMessage) throws MessageNotFoundException, DatabaseUpdateException {
		this.pipe.updateMessage(updatedMessage.getMessageID(), updatedMessage.getTitle(), updatedMessage.getContent(), updatedMessage.getReplies(),
				updatedMessage.getFatherID());
		if (!this.idsToMessagesMapping.containsKey(updatedMessage.getMessageID()))
			this.idsToMessagesMapping.put(updatedMessage.getMessageID(), updatedMessage);
	}
}