package forum.server.domainlayer;

import java.util.Collection;
import forum.server.domainlayer.search.*;
import forum.server.domainlayer.interfaces.*;
import forum.server.domainlayer.message.NotPermittedException;

import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;

/**
 * With this interface the Controller layer of the server communicates with the domain layer.  
 * 
 * This interface contains all the forum logic and provides all the forum functionalities to
 * the upper layers.
 */

/**
 * @author Vitali Sepetnitsky 
 */
public interface ForumFacade { //extends SearchEngine {

	// Guest related methods:

	/**
	 * Creates a new guest in the system and saves it in the guests set.
	 *
	 * @return
	 * 		The created guest
	 */
	public UIUser addGuest();

	/**
	 * Unregisters a guest with a given id. 
	 * 
	 * This method is used when a user stops using a guest id it was given.
	 * 
	 * @param userID
	 * 		The guest id
	 */
	public void removeGuest(final long userID); // if an exception occurred it will be handled internally

	/**
	 * @return
	 * 		The number of active forum guests - who currently view the forum contents
	 */
	public long getActiveGuestsNumber();

	// User related methods
	
	/**
	 * @return
	 * 		A collection of the currently active forum members user-names
	 */
	public Collection<String> getActiveMemberUserNames();

	/**
	 * 
	 * @return
	 *		A collection of all the forum members
	 * @throws DatabaseRetrievalException
	 * 	    If a problem has occurred while trying to retrieve the required data from the database
	 */
	public Collection<UIMember> getAllMembers() throws DatabaseRetrievalException;
	
	/**
	 * Returns the unique id of the forum member whose user-name equals to the given one
	 * 
	 * @param username
	 * 		The user-name of the member whose id should be retrieved
	 * 
	 * @return
	 * 		The id of the required member
	 * 
	 * @throws NotRegisteredException
	 * 		If a member with the given user-name isn't registered to the forum 
	 * @throws DatabaseRetrievalException
	 * 	    If a problem has occurred while trying to retrieve the required data from the database
	 */
	public long getMemberIdByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException;

	/**
	 * 
	 * logs-in a user with the given parameters
	 * 
	 * @param username
	 * 		The user-name of the required user
	 * @param password
	 * 		The password of the required user
	 * 
	 * @return
	 * 		The logged-in user data, accessible via the UIMember interface
	 * 
	 * @throws NotRegisteredException
	 * 		If a member with the given user-name isn't registered to the forum
	 * @throws WrongPasswordException
	 * 		If the given password is invalid
	 * @throws DatabaseRetrievalException 
	 * 		If a problem has occurred while trying to retrieve the required data from the database
	 */
	public UIMember login(final String username, final String password) throws NotRegisteredException,
	WrongPasswordException, DatabaseRetrievalException;

	/**
	 * 
	 * Logs out a user whose user-name equals to the given one
	 * 
	 * @param username
	 * 		The user-name of the user who should be logged out
	 *
	 * @throws NotConnectedException
	 * 		If a user with the given user-name isn't connected 
	 */
	public void logout(final String username) throws NotConnectedException;

	/**
	 * 
	 * Registers a new member, with the given parameters, to the forum
	 * 
	 * @param username
	 * 		The user-name of the new user
	 * @param password
	 * 		The password of the new user
	 * @param lastName
	 * 		The last name of the new user
	 * @param firstName
	 * 		The first name of the new user
	 * @param email
	 * 		The e-mail of the new user
	 * 
	 * @return
	 * 		The id of the new registered member
	 * 
	 * @throws MemberAlreadyExistsException
	 * 		If a user with the same user-name or e-mail already exists in the system
	 * @throws DatabaseUpdateException 
	 *		If a problem has occurred while trying to update the database of the forum
	 *
	 * Note: The registration doesn't make the user logged-in, this means that the user has to login in order to 
	 * use the privileges of a registered user
	 */
	public long registerNewMember(final String username, final String password, final String lastName,
			final String firstName, final String email) throws MemberAlreadyExistsException, DatabaseUpdateException;

	
	/**
	 * 
	 * Makes a registered forum member to be a moderator and get specific moderator permissions like edit 
	 * and delete messages
	 * 
	 * @param applicantID
	 * 		The id of the user which asks the promotion, typically the forum administrator
	 * @param username
	 * 		The user-name of the user for whom the promotion is asked
	 * 
	 * @throws NotPermittedException
	 * 		In case the asking user doesn't have the permission to promote other users to be moderators
	 * @throws NotRegisteredException
	 * 		In case the user which should be promoted to be the forum moderator, isn't registered as a forum member
	 * @throws DatabaseUpdateException
	 *		If a problem has occurred while trying to update the database of the forum
	 */
	public void promoteToBeModerator(final long applicantID, final String username) throws NotPermittedException, 
	NotRegisteredException, DatabaseUpdateException;

	// Subject related methods:	
	
	/**
	 * Finds and returns a subject whose id is equal to the given id
	 * 
	 * @param subjectID
	 * 		The id of the subject which should be retrieved
	 * 
	 * @return
	 * 		The found subject
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id wasn't found
	 * @throws DatabaseRetrievalException
	 * 	    If a problem has occurred while trying to retrieve the required data from the database
	 */
	public UISubject getSubjectByID(final long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException;
	
	
	/**
	 * Finds and returns all the subject's sub-subjects data, accessible via the UISubject interface.
	 * 
	 * @param fatherID
	 * 		The id of the root subject, whose sub-subjects' data should be returned.
	 * 		If the id is -1, then the forum root subjects data is returned
	 *
	 * @return
	 * 		A collection of all the sub-subjects of the subject with the given id, accessible via the
	 * 		UISubject interface.
	 * 
	 * @throws SubjectNotFoundException
	 *		In case the id of the father subject wasn't found
	 * @throws DatabaseRetrievalException 
	 * 		If a problem has occurred while trying to retrieve the required data from the database
	 */
	public Collection<UISubject> getSubjects(final long fatherID) throws SubjectNotFoundException, DatabaseRetrievalException;

	/**
	 * 
	 * Adds a new sub-subject under a subject whose id is the given one.
	 * 
	 * In case the fatherID is -1 the new subject will be added as one of the root subjects of the
	 * whole forum.
	 * 
	 * @param userID
	 * 		The id of the user who asks to add the new subject to the forum. 
	 * 		The given id is used in order to check if the user has the permissions to add new subjects.
	 * 		
	 * @param fatherID
	 * 		The id of the root subject (to which a new sub-subject will be added),
	 * 		can be -1 in case the subject should be added as one of the root subjects - at the top level.
	 * @param name
	 * 		The name of the new subject
	 * @param description
	 * 		The description of the new subject
	 *
	 * @return
	 * 		The created subject data accessible via a UISubject interface
	 * 
	 * @throws SubjectNotFoundException
	 *		In case the id of the father subject wasn't found
	 * @throws SubjectAlreadyExistsException
	 * 		In case a subject with the given name already exists in the level the new subject
	 * 		should be added
	 * @throws NotRegisteredException 
	 * 		In case the user who wants to add the new subject, isn't registered to the forum
	 * @throws NotPermittedException 
	 * 		In case the user who wants to add the new subject, doesn't have the permission to perform
	 * 		this operation
	 * @throws DatabaseUpdateException 
	 * 		In case the new subject can't be added to the forum database because of database connection
	 * 		errors
	 */
	public UISubject addNewSubject(final long userID, final long fatherID, final String name, final String description)
			throws SubjectNotFoundException, SubjectAlreadyExistsException, NotRegisteredException, 
			NotPermittedException, DatabaseUpdateException;


	// Thread related methods

	/**
	 * Finds and returns a thread whose id is equal to the given id
	 * 
	 * @param threadID
	 * 		The id of the thread which should be retrieved
	 * 
	 * @return
	 * 		The found thread
	 * 
	 * @throws ThreadNotFoundException
	 * 		In case a thread with the given id wasn't found
	 * @throws DatabaseRetrievalException
	 * 	    If a problem has occurred while trying to retrieve the required data from the database
	 */
	public UIThread getThreadByID(final long threadID) throws ThreadNotFoundException, DatabaseRetrievalException;

	
	/**
	 * 
	 * Finds and returns all the subject's threads data, accessible via the UIThread interface.
	 * 
	 * @param rootSubjectID
	 * 		The if of the subject whose threads should be represented
	 * 
	 * @return
	 * 		A collection of all the threads of the subject with the given id, accessible via the
	 * 		UIThread interface.
	 * 
	 * @throws SubjectNotFoundException
	 *		In case the id of the father subject wasn't found
	 * @throws DatabaseRetrievalException 
	 * 		If a problem has occurred while trying to retrieve the required data from the database
	 */
	public Collection<UIThread> getThreads(final long rootSubjectID) throws SubjectNotFoundException, DatabaseRetrievalException;

	/**
	 * Opens a new thread under the given subject and adds a new message as its root
	 * 
	 * @param userID
	 * 		The id of the user who asks to open the new thread.
	 * 		The given id is used in order to check whether the user has the permission to open
	 * 		new threads in the forum.
	 * @param subjectID
	 * 		The id of the subject under which the new thread should be created
	 * @param topic
	 * 		The topic of the new thread
	 * @param title	
	 * 		The title of the new thread's root message
	 * @param content
	 * 		The content of the new thread's root message
	 * 
	 * @return
	 * 		The created thread data, accessible via a UIThread interface
	 * 
	 * @throws NotRegisteredException 
	 * 		In case the user who wants open the thread, isn't registered as a member of the forum
	 * @throws NotPermittedException 
	 * 		In case the user who wants to open the thread, doesn't have the permission to perform this
	 * 		operation
	 * @throws SubjectNotFoundException
	 *		In case the id of the father subject wasn't found
	 * @throws DatabaseUpdateException 
	 * 		In case the new thread can't be added to the database due to a database connection error 		
	 */
	public UIThread openNewThread(final long userID, final long subjectID, final String topic, final String title,
			final String content) throws NotRegisteredException, NotPermittedException, SubjectNotFoundException, 
			DatabaseUpdateException;

	public UIThread updateAThread(final long userID, final long threadID, final String newTopic) throws NotRegisteredException, 
	NotPermittedException, ThreadNotFoundException, DatabaseUpdateException;
	
	// Message related methods:
	
	/**
	 * @see
	 * 		SearchEngine#searchByAuthor(long, int, int)
	 */
	public SearchHit[] searchByAuthor(long usrID, int from, int to);
	
	/**
	 * @see
	 * 		SearchEngine#searchByContent(String, int, int)
	 */
	public SearchHit[] searchByContent(String phrase, int from, int to);
	
	/**
	 * Finds and returns a message whose id is equal to the given id
	 * 
	 * @param messageID
	 * 		The id of the message which should be retrieved
	 * 
	 * @return
	 * 		The found message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id wasn't found
	 * @throws DatabaseRetrievalException
	 * 	    If a problem has occurred while trying to retrieve the required data from the database
	 */
	public UIMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException;
	
	/**
	 * Returns a collection of messages, accessible via the UIMessage interface, whose author id
	 * is the given one.
	 * 
	 * @param authorID
	 * 		The id of the author of the messages which should be retrieved
	 * 
	 * @return
	 * 		A collection of messages whose author's id is the given one
	 * 
	 * @throws NotRegisteredException
	 * 		In case a member with the given id isn't registered as a forum member
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the database
	 */
	public Collection<UIMessage> getMessagesByUserID(final long authorID) throws DatabaseRetrievalException,
	NotRegisteredException;

	/**
	 * 
	 * Finds and returns all the replies data, accessible via the UIMessage interface.
	 * 
	 * @param fatherID
	 * 		The id of the message whose replies should be represented
	 * 
	 * @return
	 * 		A collection of all the replies of the message with the given id, accessible via the
	 * 		UIMessage interface.
	 * 
	 * @throws MessageNotFoundException
	 *		In case the id of the root message wasn't found
	 * @throws DatabaseRetrievalException 
	 * 		In case the required data can't be retrieved from the database
	 */
	public Collection<UIMessage> getReplies(final long fatherID) throws MessageNotFoundException, DatabaseRetrievalException;
	
	/**
	 *
	 * Adds a new message as a reply to the given one - doesn't open a new thread
	 * 
	 * @param authorID
	 * 	 	The id of the reply author
	 * @param fatherID
	 * 		A message to which the reply should be added 
	 * @param title
	 * 		The title of the new reply
	 * @param content
	 * 		The content of the new reply
	 * 
	 * @return
	 * 		The data of the new reply, accessible via a UIMessage interface
	 * 
	 * @throws NotRegisteredException 
	 * 		In case the user who wants to add the reply, isn't registered as a forum member
	 * @throws NotPermittedException 
	 * 		In case the user who wants to add the reply isn't permitted to perform this operation
	 * @throws MessageNotFoundException
	 * 		In case the root message wasn't found
	 * @throws DatabaseUpdateException 
	 * 		In case the new reply data can't be added to the database due to a database connection error
	 */
	public UIMessage addNewReply(final long authorID, final long fatherID, final String title,
			final String content) throws NotRegisteredException, NotPermittedException, MessageNotFoundException,
			DatabaseUpdateException;

	/**
	 * 
	 * Finds and updates a message with the given id, with the new title and content
	 *
	 * @param userID
	 * 		The id of the user who wants to update the message
	 * @param messageID
	 * 		The id of the message which should be updated
	 * @param newTitle
	 * 		The new title of the message
	 * @param newContent
	 * 		The new content of the message
	 * 
	 * @return
	 * 		The updated message, accessible via a UIMessage interface
	 * 
	 * @throws NotRegisteredException 
	 * 		In case the user who wants to update the message, isn't registered as a forum member
	 * @throws NotPermittedException 
     *		In case the user who wants to update the message doesn't have the permission to perform
     *		this operation
	 * @throws MessageNotFoundException
	 * 		In case the message wasn't found in the database
     * @throws DatabaseUpdateException 
     * 		In case the message can't be updated in the database due to a database connection error 
	 */
	public UIMessage updateAMessage(final long userID, final long messageID, final String newTitle, 
			final String newContent) throws NotRegisteredException, NotPermittedException, 
			MessageNotFoundException, DatabaseUpdateException;

	/**
	 * 
	 * Finds and deletes a message with the given id
	 * 
	 * @param userID
	 * 		The id of the user who requests to delete the message
	 * @param fatherID
	 * 		The id of the message father
	 * @param messageID
	 * 		The id of the message to be deleted
	 * 
	 * @throws NotRegisteredException 
	 * 		In case the user who wants to delete the message, isn't registered as a forum member
	 * @throws NotPermittedException 
	 * 		In case the user who wants to delete the message doesn't have a permission to perform
	 * 		this operation
	 * @throws MessageNotFoundException
	 * 		In case the message which should be deleted wasn't found in the database
	 * @throws DatabaseUpdateException
	 * 		In case the message can't be deleted from the database due to a database connection error 
	 */
	public void deleteAMessage(final long userID, final long fatherID, final long messageID)
	throws NotRegisteredException, NotPermittedException, MessageNotFoundException, DatabaseUpdateException;
		
}
	// Update related messages:

	// TODO: void updatePassword(long userId, String oldPassword, String newPassword);

	// TODO: void updateMemberDetails( ... )

	// Deletion related messages:

	// TODO: void deleteThread(final long userId, final long dirId, final long threadId);
	
	// Search related methods:

	//TODO: Set<SearchResult> searchByContent(final String message);

	// TODO: Set<SearchResult> searchByAuthor(final String authorName);

	// TODO: Set<SearchResult> searchByDate(final Date fromDate, final Date toDate);

	// and there can be more and more ...