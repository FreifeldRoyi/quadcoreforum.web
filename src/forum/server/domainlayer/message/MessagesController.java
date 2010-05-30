/**
 * This class is a controller for all the forum content actions.
 *
 * Contains methods to get subjects, threads and messages location as domain objects.
 *
 * In addition, this class holds all the methods that are needed by the GUI to access messages
 * and present the forum pages (like getting content of subjects and threads by their id-s) 
 * and all other administrative methods of adding and deleting subjects, threads and messages.
 */
package forum.server.domainlayer.message ;

import java.util.*;

import forum.server.domainlayer.*;
import forum.server.domainlayer.interfaces.*;
import forum.server.domainlayer.user.*;

import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;


import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

public class MessagesController {

	private ForumDataHandler dataHandler;

	/**
	 * The class constructor
	 * 
	 * Initializes an instance of messages controller, that handles all the forum content activity
	 */
	public MessagesController(ForumDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	// Subject related methods

	/**
	 * @see
	 * 		ForumFacade#getSubjectByID(long)
	 */
	public UISubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		return this.dataHandler.getMessagesCache().getSubjectByID(subjectID);
	}

	/**
	 * @see
	 * 		ForumFacade#getSubjects(long)
	 */
	public Collection<UISubject> getSubjects(final long fatherID) throws SubjectNotFoundException, 
	DatabaseRetrievalException {
		final String tLoggerMessage = fatherID != -1 ? "Sub-subjects of a subject with id " + fatherID + " are requested to view." :
			"The forum top-level subjects are requested to view";
		SystemLogger.info(tLoggerMessage);
		final Collection<UISubject> toReturn = new Vector<UISubject>();

		if (fatherID == -1)
			toReturn.addAll(this.dataHandler.getMessagesCache().getTopLevelSubjects());
		else {
			final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);
			final Collection<Long> tSubSubjectsIDs = tFatherSubject.getSubSubjects();
			for (long tSubjectID : tSubSubjectsIDs)
				toReturn.add(this.dataHandler.getMessagesCache().getSubjectByID(tSubjectID));
		}
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#addNewSubject(long, long, String, String)
	 */
	public UISubject addNewSubject(final long userID, final long fatherID, final String name, final String description) throws 
	NotRegisteredException, NotPermittedException, SubjectAlreadyExistsException, SubjectNotFoundException, DatabaseUpdateException{
		try {
			String tLoggerMessageEnd = fatherID == -1 ? "the top level of the forum." : "subject with id " + fatherID;
			Permission tPermissionToCheck = fatherID == -1 ? Permission.ADD_SUBJECT : Permission.ADD_SUB_SUBJECT;

			SystemLogger.info("A user with id " + userID + " requests to add a new subject named " + name + " to " + 
					tLoggerMessageEnd + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(tPermissionToCheck)) {
				SystemLogger.info("permission granted for user " + userID + ".");
				// checks that there doesn't exist a subject whose id is same as the given one, in the required level
				Collection<UISubject> tRequiredLevelSubjects = this.getSubjects(fatherID);
				for (UISubject tCurrentSubject : tRequiredLevelSubjects)
					if (tCurrentSubject.getName().equals(name))
						throw new SubjectAlreadyExistsException(name);
				// adds the new subject to the forum
				if (fatherID == -1) {
					ForumSubject toReturn = this.dataHandler.getMessagesCache().createNewSubject(name, description, fatherID);
					SystemLogger.info("A subject named " + name + " was added to the top level of the forum");
					return toReturn;
				}	
				else {
					ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);
					ForumSubject tNewSubject = this.dataHandler.getMessagesCache().createNewSubject(name, description, fatherID);
					tFatherSubject.addSubSubject(tNewSubject.getID());
					this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);
					SystemLogger.info("A subject named " + name + " was added as a sub-subject of a subject named " + 
							tFatherSubject.getName());
					return tNewSubject;
				}
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, tPermissionToCheck);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Thread related methods

	/**
	 * @see
	 * 		ForumFacade#getThreadByID(long)
	 */
	public UIThread getThreadByID(long thread) throws ThreadNotFoundException, DatabaseRetrievalException {
		return this.dataHandler.getMessagesCache().getThreadByID(thread);
	}

	/**
	 * @see
	 * 		ForumFacade#getThreads(long)
	 */
	public Collection<UIThread> getThreads(final long fatherID) throws SubjectNotFoundException, DatabaseRetrievalException {
		SystemLogger.info("Threads of a subject with id " + fatherID + " are requested to view.");
		final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);
		final Collection<Long> tThreadsIDs = tFatherSubject.getThreads();
		final Collection<UIThread> toReturn = new Vector<UIThread>();
		for (long tThreadID : tThreadsIDs) {
			try {
				toReturn.add(this.dataHandler.getMessagesCache().getThreadByID(tThreadID));
			}
			catch (ThreadNotFoundException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#openNewThread(long, long, String, String, String)
	 */
	public UIThread openNewThread(final long userID, final String topic, final long subjectID, 
			final String title, final String content) throws NotRegisteredException, NotPermittedException, 
			SubjectNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to open a new thread under the subject with id " +
					subjectID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.OPEN_THREAD)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(subjectID);
				final ForumMessage tNewMessage = this.dataHandler.getMessagesCache().createNewMessage(userID, title, content, -1);
				final ForumThread tNewThread = this.dataHandler.getMessagesCache().openNewThread(topic, 
						tNewMessage.getMessageID(), tFatherSubject.getID());
				tFatherSubject.addThread(tNewThread.getID());
				this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);
				return tNewThread;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.OPEN_THREAD);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public void deleteAThread(final long userID, final long fatherID, final long threadID) throws NotRegisteredException, 
	NotPermittedException, SubjectNotFoundException, ThreadNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to delete ther thread with id " +
					threadID + " from a subject with id " + fatherID);
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.DELETE_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumThread tThreadToDelete = this.dataHandler.getMessagesCache().getThreadByID(threadID);
				final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);

				// delete the thread from the desired subject
				tFatherSubject.deleteThread(threadID);
				this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);

				this.dataHandler.getMessagesCache().deleteATread(tThreadToDelete.getID());

				SystemLogger.info("A thread with id " + threadID + " was deleted successfuly from the subject " +
						fatherID + " by a user " + userID);
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.DELETE_THREAD);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}

	}
	
	public UIThread updateAThread(final long userID, final long threadID, final String newTopic) throws NotRegisteredException,
	NotPermittedException, ThreadNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to edit a thread with id " +
					threadID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			final ForumThread tThreadToEdit = this.dataHandler.getMessagesCache().getThreadByID(threadID);
			if (tApplicant.isAllowed(Permission.EDIT_SUBJECT)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				tThreadToEdit.updateMe(newTopic);
				this.dataHandler.getMessagesCache().updateInDatabase(tThreadToEdit);
				return tThreadToEdit;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.EDIT_MESSAGE);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Message related methods:

	/**
	 * @see
	 * 		ForumFacade#getMessageByID(long)
	 */
	public UIMessage getMessageByID(final long messageID)
	throws MessageNotFoundException, DatabaseRetrievalException {
		return this.dataHandler.getMessagesCache().getMessageByID(messageID);
	}

	/**
	 * @see 
	 * 		ForumFacade#getReplies(long)
	 */
	public Collection<UIMessage> getReplies(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		SystemLogger.info("Replies of a message with id " + messageID + " are requested to view.");
		final ForumMessage tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(messageID);
		final Collection<Long> tRepliesIDs = tFatherMessage.getReplies();
		final Collection<UIMessage> toReturn = new Vector<UIMessage>();
		for (long tReplyID : tRepliesIDs) {
			try {
				toReturn.add(this.dataHandler.getMessagesCache().getMessageByID(tReplyID));
			}
			catch (MessageNotFoundException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}	

	/**
	 * @see
	 * 		ForumFacade#addNewReply(long, long, String, String)
	 */
	public UIMessage addNewReply(final long userID, final long fatherID, final String title,
			final String content) throws NotRegisteredException, NotPermittedException,
			MessageNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to add a new reply to a message with id " +
					fatherID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			final ForumMessage tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(fatherID);
			if (tApplicant.isAllowed(Permission.REPLY_TO_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumMessage tNewMessage = this.dataHandler.getMessagesCache().createNewMessage(userID, title, content, fatherID);
				// adds the new reply to the replied message
				tFatherMessage.addReply(tNewMessage.getMessageID());
				this.dataHandler.getMessagesCache().updateInDatabase(tFatherMessage);
				SystemLogger.info("A new reply was successfuly added to message " + fatherID + " by a user " + userID + ".");
				return tNewMessage;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.REPLY_TO_MESSAGE);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		ForumFacade#updateAMessage(long, long, String, String)
	 */
	public UIMessage updateAMessage(final long userID, final long messageID, final String newTitle, 
			final String newContent) throws NotRegisteredException, NotPermittedException, MessageNotFoundException,
			DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to edit a message with id " +
					messageID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			final ForumMessage tMessageToEdit = this.dataHandler.getMessagesCache().getMessageByID(messageID);
			if (tApplicant.isAllowed(Permission.DELETE_MESSAGE) ||
					(tApplicant.isAllowed(Permission.EDIT_MESSAGE) && tMessageToEdit.getAuthorID() == tApplicant.getID())) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				tMessageToEdit.updateMe(newTitle, newContent);
				this.dataHandler.getMessagesCache().updateInDatabase(tMessageToEdit);
				return tMessageToEdit;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.EDIT_MESSAGE);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		ForumFacade#deleteAMessage(long, long, long)
	 */
	public void deleteAMessage(final long userID, final long fatherID, final long messageID) throws NotRegisteredException, 
	MessageNotFoundException, NotPermittedException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to delete a message with id " +
					messageID + " from being a reply of " + fatherID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.DELETE_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumMessage tMessageToDelete = this.dataHandler.getMessagesCache().getMessageByID(messageID);
				if (fatherID != -1) { // root message
					final ForumMessage tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(fatherID);
					tFatherMessage.deleteReply(tMessageToDelete.getMessageID());
					this.dataHandler.getMessagesCache().updateInDatabase(tFatherMessage);
					this.dataHandler.getMessagesCache().deleteAMessage(messageID);
					SystemLogger.info("A message with id " + messageID + " was deleted successfuly from the message " +
							fatherID + " by a user " + userID);
				}
				else {
					// if the message is a root message its id is same as its thread id
					try {
						this.dataHandler.getMessagesCache().deleteATread(tMessageToDelete.getMessageID());
					}
					catch (ThreadNotFoundException e) {
						System.out.println("dddddddddddddddd");
						throw new MessageNotFoundException(messageID);
					}
					SystemLogger.info("A message with id " + messageID + " was deleted successfuly from the message " +
							fatherID + " by a user " + userID);
				}
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.DELETE_MESSAGE);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}