package forum.server.updatedpersistentlayer.pipe.message;

import java.util.*;

import org.hibernate.*;
import org.hibernate.classic.Session;

import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;
import forum.server.domainlayer.message.*;

/**
 * This class is responsible of performing the operations of reading from and writing to the database
 * all the data which refers to the forum content, which means the forum subjects, threads and messages
 */

/**
 * 
 * @author Sepetnitsky Vitali
 *
 */

public class MessagesPersistenceHandler {

	// TODO: The next two methods are common to the users and messages handler, consider to make one class with static methods

	private Session getSessionAndBeginTransaction(SessionFactory ssFactory) throws DatabaseRetrievalException {
		try {
			Session toReturn = ssFactory.getCurrentSession();
			toReturn.beginTransaction();
			return toReturn;
		}
		catch (RuntimeException e) {
			throw new DatabaseRetrievalException();
		}
	}

	private void commitTransaction(Session session) throws DatabaseUpdateException {
		try {
			session.getTransaction().commit();
		}
		catch (RuntimeException e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				try {
					// Second try catch as the roll-back could fail as well
					session.getTransaction().rollback();
				}
				catch (HibernateException e1) {
					// add logging
				}
			}
			throw new DatabaseUpdateException();
		}
	}

	// subject related methods

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeSubjectID()
	 */
	@SuppressWarnings("unchecked")
	public long getFirstFreeSubjectID(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select max(subjectID) from SubjectType";
		List<Long> tResult = (List<Long>)session.createQuery(query).list();
		if (tResult.get(0) != null)
			toReturn = tResult.get(0).longValue();
		try {
			this.commitTransaction(session);
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return ++toReturn;
	}

	/**
	 * @param data
	 * 		The forum type from which the data should be read
	 * 
	 * @throws DatabaseRetrievalException 
	 * 
	 * @see
	 * 		PersistenceDataHandler#getTopLevelSubjects()
	 */
	@SuppressWarnings("unchecked")
	public Collection<ForumSubject> getTopLevelSubjects(SessionFactory ssFactory) throws DatabaseRetrievalException {
		Collection<ForumSubject> toReturn = new Vector<ForumSubject>();
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "from SubjectType where SubjectID != -1 AND FatherID = -1";
		List tResult = session.createQuery(query).list();
		for (SubjectType tCurrentSubjectType : (List<SubjectType>)tResult) 
			toReturn.add(PersistentToDomainConverter.convertSubjectTypeToForumSubject(tCurrentSubjectType));
		try {
			this.commitTransaction(session);
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return toReturn;
	}

	/**
	 * Performs a lookup in the database and returns a {@link SubjectType} object, whose id equals to the given one
	 * 
	 * @param data
	 * 		The forum data from in which the required subject should be found
	 * @param subjectID
	 * 		The id of the subject which should be found
	 * 
	 * @return
	 * 		The found subject
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id doesn't exist in the database
	 */
	public SubjectType getSubjectTypeByID(Session session, 
			long subjectID) throws DatabaseRetrievalException {
		try {
			return (SubjectType)session.get(SubjectType.class, subjectID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum type from which the data should be read
	 * 
	 * @see
	 * 		PersistenceDataHandler#getSubjectByID(long)
	 */
	public ForumSubject getSubjectByID(SessionFactory ssFactory,
			long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			SubjectType toConvert = this.getSubjectTypeByID(session, subjectID);
			if (toConvert == null) {
				this.commitTransaction(session);
				throw new SubjectNotFoundException(subjectID);
			}
			else {
				ForumSubject toReturn = PersistentToDomainConverter.convertSubjectTypeToForumSubject(toConvert);
				this.commitTransaction(session);
				return toReturn;
			}
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum data to which the new created subject should be added
	 * @throws DatabaseUpdateException 
	 * 
	 * @see
	 * 		PersistenceDataHandler#addNewSubject(long, String, String, boolean)
	 */
	public void addNewSubject(SessionFactory ssFactory, long subjectID, String name, String description, 
			long fatherID) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			SubjectType tNewSubjectType = ExtendedObjectFactory.createSubject(subjectID, name, description, fatherID);
			session.save(tNewSubjectType);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @param data
	 * 		The forum data in which the required subject should be updated
	 * @throws DatabaseUpdateException 
	 * 
	 * @see
	 * 		PersistenceDataHandler#updateSubject(long, Collection, Collection)
	 */
	public void updateSubject(SessionFactory ssFactory, long id, Collection<Long> subSubjects,
			Collection<Long> threads) throws SubjectNotFoundException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			SubjectType tSubjectToUpdate = this.getSubjectTypeByID(session, id);
			// update the sub-subjects
			tSubjectToUpdate.getSubSubjectsIDs().clear();
			tSubjectToUpdate.getSubSubjectsIDs().addAll(subSubjects);
			// update the threads
			tSubjectToUpdate.getThreadsIDs().clear();
			tSubjectToUpdate.getThreadsIDs().addAll(threads);
			session.update(tSubjectToUpdate);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Thread related methods

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * @throws DatabaseRetrievalException 
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeThreadID()
	 */
	@SuppressWarnings("unchecked")
	public long getFirstFreeThreadID(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select max(threadID) from ThreadType";
		List<Long> tResult = (List<Long>)session.createQuery(query).list();
		if (tResult.get(0) != null)
			toReturn = tResult.get(0).longValue();
		try {
			this.commitTransaction(session);
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}		
		return ++toReturn;
	}

	/**
	 * @param data
	 * 		The forum data from which the required thread should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getThreadByID(long)
	 */
	public ForumThread getThreadByID(SessionFactory ssFactory, long threadID) throws ThreadNotFoundException, DatabaseRetrievalException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			ThreadType toConvert = this.getThreadTypeByID(session, threadID);
			if (toConvert == null) {
				this.commitTransaction(session);
				throw new ThreadNotFoundException(threadID);
			}
			else {
				ForumThread toReturn = PersistentToDomainConverter.convertThreadTypeToForumThread(toConvert);
				this.commitTransaction(session);
				return toReturn;
			}
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * Performs a lookup in the database and returns a {@link ThreadType} object, whose id equals to the given one
	 * 
	 * @param data
	 * 		The forum data from in which the required thread should be found
	 * @param threadID
	 * 		The id of the thread which should be found
	 * 
	 * @return
	 * 		The found thread
	 * 
	 * @throws ThreadNotFoundException
	 * 		In case a thread with the given id doesn't exist in the database
	 * @throws DatabaseRetrievalException 
	 */
	private ThreadType getThreadTypeByID(Session session,
			long threadID) throws DatabaseRetrievalException {
		try {
			return (ThreadType)session.get(ThreadType.class, threadID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum data in which the required thread should be opened
	 * 
	 * @see
	 * 		PersistenceDataHandler#openNewThread(long, String, long)
	 */
	public void openNewThread(SessionFactory ssFactory, long threadID, String topic, 
			long rootID, long fatherSubjectID) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			ThreadType tNewThreadType = ExtendedObjectFactory.createThreadType(threadID, topic, rootID, fatherSubjectID);
			session.save(tNewThreadType);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required thread should be deleted
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAThread(long)
	 */
	public Collection<Long> deleteAThread(SessionFactory ssFactory, 
			long threadID) throws ThreadNotFoundException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			ThreadType tThreadToDelete = this.getThreadTypeByID(session, threadID);
			if (tThreadToDelete == null) {
				this.commitTransaction(session);
				throw new ThreadNotFoundException(threadID);				
			}
			else {
				long tRootMessageID = tThreadToDelete.getStartMessageID();
				Collection<Long> toReturn = this.findMessageAndRepliesIDs(ssFactory, tRootMessageID);
				session.delete(tThreadToDelete);
				try {
					this.deleteAMessage(ssFactory, tRootMessageID);
				}
				catch (MessageNotFoundException e) {
					this.commitTransaction(session);
				}
				return toReturn;
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public void updateThread(SessionFactory ssFactory, long threadID, String topic) throws ThreadNotFoundException, 
	DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			try {
				ThreadType tThreadToEdit = this.getThreadTypeByID(session, threadID);
				if (tThreadToEdit == null) {
					this.commitTransaction(session);
					throw new ThreadNotFoundException(threadID);
				}
				tThreadToEdit.setTopic(topic);
				this.commitTransaction(session);
			}
			catch (DatabaseUpdateException e) {
				this.commitTransaction(session);
				throw e;
			}	
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}	

	// Message related methods

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMessageID()
	 */
	@SuppressWarnings("unchecked")
	public long getFirstFreeMessageID(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select max(messageID) from MessageType";
		List<Long> tResult = (List<Long>)session.createQuery(query).list();
		if (tResult.get(0) != null)
			toReturn = tResult.get(0).longValue();
		try {
			this.commitTransaction(session);
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return ++toReturn;
	}

	/**
	 * @param data
	 * 		The forum data from which the required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getAllMessages()
	 */
	@SuppressWarnings("unchecked")
	public Collection<ForumMessage> getAllMessages(SessionFactory ssFactory) throws DatabaseRetrievalException {
		Collection<ForumMessage> toReturn = new Vector<ForumMessage>();
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "from MessageType where MessageID != -1";
		List tResult = session.createQuery(query).list();
		for (MessageType tCurrentMessageType : (List<MessageType>)tResult) 
			toReturn.add(PersistentToDomainConverter.convertMessageTypeToForumMessage(tCurrentMessageType));
		try {
			this.commitTransaction(session);
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return toReturn;
	}

	/**
	 * @param data
	 * 		The forum data from which the required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAMessage(long)
	 */
	public ForumMessage getMessageByID(SessionFactory ssFactory, 
			long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			MessageType toConvert = this.getMessageTypeByID(session, messageID);
			if (toConvert == null) {
				this.commitTransaction(session);
				throw new MessageNotFoundException(messageID);
			}
			else {
				ForumMessage toReturn = PersistentToDomainConverter.convertMessageTypeToForumMessage(toConvert);
				this.commitTransaction(session);
				return toReturn;
			}
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * Performs a lookup in the database and returns a {@link MessageType} object, whose id equals to the given one
	 * 
	 * @param data
	 * 		The forum data from in which the required message should be found
	 * @param messageID
	 * 		The id of the message which should be found
	 * 
	 * @return
	 * 		The found message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id doesn't exist in the database
	 */
	private MessageType getMessageTypeByID(Session session, 
			long messageID) throws DatabaseRetrievalException {
		try {
			return (MessageType)session.get(MessageType.class, messageID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}	

	/**
	 * @param data
	 * 		The forum data to which the new message should be added
	 * 
	 * @see
	 * 		PersistenceDataHandler#addNewMessage(long, long, String, String)
	 */
	public void addNewMessage(SessionFactory ssFactory, long messageID, long authorID, 
			String title, String content, long fatherID) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MessageType tNewMessageType = ExtendedObjectFactory.createMessageType(messageID, authorID, title, content, fatherID);
			session.save(tNewMessageType);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required thread should be deleted
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAMessage(long)
	 */
	public void updateMessage(SessionFactory ssFactory, long messageID, 
			String newTitle, String newContent, 
			Collection<Long> replies, long fatherID) throws MessageNotFoundException, DatabaseUpdateException {		
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MessageType tMsgToEdit = this.getMessageTypeByID(session, messageID);
			if (tMsgToEdit == null) {
				this.commitTransaction(session);
				throw new MessageNotFoundException(messageID);
			}
			tMsgToEdit.setTitle(newTitle);
			tMsgToEdit.setContent(newContent);
			tMsgToEdit.setFatherID(fatherID);
			try {
				//this.updateThreadOfReplies(session, tMsgToEdit, replies, threadID);
				tMsgToEdit.setRepliesIDs(new HashSet<Long>(replies));
				session.update(tMsgToEdit);

				this.commitTransaction(session);
			}
			catch (DatabaseUpdateException e) {
				this.commitTransaction(session);
				throw e;
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/*	private void updateThreadOfReplies(Session session, 
			MessageType toUpdate, Collection<Long> newReplies, long threadID) throws DatabaseUpdateException {
		try {
			for (Long tCurrentReplyID : newReplies)
				if (!toUpdate.getRepliesIDs().contains(tCurrentReplyID)) {
					MessageType tCurrentReply = this.getMessageTypeByID(session, tCurrentReplyID);
					tCurrentReply.setThreadID(threadID);
					session.update(tCurrentReply);
				}
		}
		catch (Exception e) {
			throw new DatabaseUpdateException();
		}
	}*/

	/**
	 * @param data
	 * 		The forum data from which required message should be deleted
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAMessage(long)
	 */	
	public Collection<Long> deleteAMessage(SessionFactory ssFactory, 
			long messageID) throws MessageNotFoundException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			try {
				Collection<Long> tMessagesIDsToDelete = this.findMessageAndRepliesIDs(ssFactory, messageID);
				MessageType tMessageType = this.getMessageTypeByID(session, messageID);
				if (tMessageType == null)
					throw new MessageNotFoundException(messageID);
				session.delete(tMessageType);
				/*
				for (Long tReplyIDToDelete : tMessagesIDsToDelete) {
					if (tReplyIDToDelete != messageID) {
						MessageType tCurrentReply = this.getMessageTypeByID(session, tReplyIDToDelete);
						try {
							session.delete(tCurrentReply);
						}
						catch (HibernateException e) {
							//TODO: add logging
						}
					}
				}*/
				this.commitTransaction(session);			
				return tMessagesIDsToDelete;
			}
			catch (DatabaseRetrievalException e) {
				this.commitTransaction(session);
				throw new DatabaseUpdateException();
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/* if the message wasn't found - an empty collection will be returned. */
	private Collection<Long> findMessageAndRepliesIDs(SessionFactory ssFactory, 
			long messageID) throws DatabaseRetrievalException {
		Collection<Long> toReturn = new Vector<Long>();
		MessageType tFatherMessage = this.getMessageTypeByID(ssFactory.getCurrentSession(), messageID);
		if (tFatherMessage != null) {
			for (long tReplyID : tFatherMessage.getRepliesIDs()) {
				toReturn.addAll(this.findMessageAndRepliesIDs(ssFactory, tReplyID));
			}
			toReturn.add(messageID);
		}
		return toReturn;
	}
}