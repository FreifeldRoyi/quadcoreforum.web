/**
 * This class represents a thread of the forum which is a container of its root message
 * and other statistical information
 */
package forum.server.domainlayer.message;

import forum.server.domainlayer.interfaces.UIThread;

public class ForumThread implements UIThread {

	private long threadID;

	private long rootMessageID;
	private long latestPostID;
	private String topic;
	private long numOfResponses;
	private long numOfViews;
	private long fatherSubjectID;

	// TODO: handle numOfResponses, numOfViews, latestPostID

	/**
	 * A full constructor of a forum-thread.
	 * 
	 * This constructor is used while creating a thread according to an existing thread (from the database)
	 * 
	 * @param threadID
	 * 		The id of the thread
	 * @param topic
	 * 		The topic of the thread
	 * @param rootID
	 * 		The id of the thread root message
	 * @param numOfReponses
	 * 		The number of responses to one of the thread messages
	 * @param numOfViews
	 * 		The number of views of the thread content
	 */
	public ForumThread(final long threadID, final String topic, final long rootID, int numOfReponses, int numOfViews,
			long fatherSubjectID)
	{
		this.threadID = threadID;
		this.topic = topic;
		this.rootMessageID = rootID;
		this.numOfResponses = numOfReponses;
		this.numOfViews = numOfViews;
		this.fatherSubjectID = fatherSubjectID;
	}

	/**
	 * A constructor of a new forum thread, which doesn't currently exist in the database and therefore some of the
	 * attributes are initialized to default values.
	 *
	 * @param threadID
	 * 		The id of the new thread
	 * @param topic
	 * 		The topic of the new thread
	 * @param rootID
	 * 		The id of the thread root message
	 */
	public ForumThread(final long threadID, final String topic, final long rootID, final long fatherSubjectID) {
		this(threadID, topic, rootID, 0, 0, fatherSubjectID);
	}

	// getters

	/**
	 * @see
	 * 		UIThread#getID()
	 */
	public long getID() {
		return this.threadID;
	}

	/**
	 * @see
	 * 		UIThread#getTopic()
	 */
	public String getTopic() {
		return this.topic;
	}

	/**
	 * 
	 * @see
	 * 		UIThread#getRootMessageID()
	 */
	public long getRootMessageID() {
		return this.rootMessageID;
	}

	/**
	 * @see
	 * 		UIThread#getNumOfResponses()
	 */
	public long getNumOfResponses() {
		return this.numOfResponses;
	}

	/**
	 * @see
	 * 		UIThread#getNumOfViews()
	 */
	public long getNumOfViews() {
		return this.numOfViews;
	}

	public long getFatherID() {
		return this.fatherSubjectID;
	}
	
	/**
	 * @see
	 * 		UIThread#toString()
	 */
	public String toString() {
		return this.getID() + "\t" + this.getRootMessageID() + "\t" + this.getTopic() + "\t" +
		this.getNumOfResponses() + "\t" + this.getNumOfViews();
	}

	// methods	

	/**
	 * Increases the number of responses to the thread's content
	 */
	public void incNumOfResponses() {
		this.numOfResponses++;
	}

	/**
	 * Decreases the number of responses to the thread's content
	 */
	public void decNumOfResponses() {
		if (this.numOfResponses > 0)
			this.numOfResponses--;
	}

	/**
	 * Increases the number of views of the thread's content 
	 */
	public void incNumOfViews() {
		this.numOfViews++;
	}
	
	public void updateMe(String newTopic) {
		this.topic = newTopic;
	}
}
