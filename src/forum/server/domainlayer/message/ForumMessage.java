package forum.server.domainlayer.message;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

import forum.server.domainlayer.interfaces.UIMessage;

@Searchable //(extend = "UIMessage")
public class ForumMessage implements UIMessage {
	
	@SearchableId
	private long messageID;
	
	@SearchableProperty (name = "authorID")
	private long authorID;
	
	@SearchableProperty (name = "title")
	private String title;
	
	@SearchableProperty (name = "content")
	private String content;
	
	private long fatherID;
	
	private GregorianCalendar postTime;

	// the ids of the message replies
	private Collection<Long> repliesIDs;

	/**
	 * 
	 * A full constructor of the message, which initializes all its attributes with given values
	 * 
	 * This constructor is used while creating a message according to an existing message (from the database)
	 * 
	 * @param messageID
	 * 		The id of the new message
	 * @param authorID
	 * 		The id of the message author
	 * @param title
	 * 		The title of the new message
	 * @param content
	 * 		The content of the new message
	 * @param postTime
	 * 		The time where the new message was posted
	 * @param replies
	 * 		A collection of the message replies' ids
	 */
	public ForumMessage(final long messageID, final long authorID, String title, String content, 
			GregorianCalendar postTime, Collection<Long> replies, final long fatherID) {
		this(messageID, authorID, title, content, fatherID);		
		this.setPostTime(postTime);
		System.out.println(replies);
		this.repliesIDs.addAll(replies);
	}

	/**
	 * 
	 * A constructor of a new message which hasn't exist yet in the database. 
	 * 
	 * The constructor initializes the message posting time and replies collection, to be default
	 * values. 
	 * 
	 * @param messageID
	 * 		The id of the new message
	 * @param authorID
	 * 		The id of the message author
	 * @param title
	 * 		The title of the new message
	 * @param content
	 * 		The content of the new message
	 */
	public ForumMessage(final long messageID, final long authorID, String title, String content, long fatherID) {
		this.messageID = messageID;
		this.authorID = authorID;
		this.setTitle(title);
		this.setContent(content);
		this.postTime = new GregorianCalendar();
		this.repliesIDs = new Vector<Long>();
		this.fatherID = fatherID;
	}

	// getters
	
	/**
	 * @see
	 * 		UIMessage#getMessageID()
	 */
	//@SearchableId
	public long getMessageID() {
		return this.messageID;
	}

	/* used for the compass */
	
	public ForumMessage() {}
	
	public void setMessageID(long messageID) {
		this.messageID = messageID;
	}

	/* used for the compass */
	public void setAuthorID(long authorID) {
		this.authorID = authorID;
	}
	
	public void setFatherID(long fatherID) {
		this.fatherID = fatherID;
	}
	
	public void setRepliesIDs(Collection<Long> repliesIDs) {
		this.repliesIDs = repliesIDs;
	}
	
	
	/**
	 * @see
	 * 		UIMessage#getAuthorID()
	 */
	//@SearchableProperty (name = "authorID")
	public long getAuthorID() {
		return this.authorID;
	}

	/**
	 * @see
	 * 		UIMessage#getTitle()
	 */
	//@SearchableProperty (name = "title")
	public String getTitle() {
		return this.title;
	}

	/**
	 * @see
	 * 		UIMessage#getContent()
	 */
	//@SearchableProperty (name = "content")
	public String getContent() {
		return this.content;
	}
	
	/**
	 * @see
	 * 		UIMessage#getDate()
	 */
	public String getDate() {
		String toReturn = this.postTime.get(Calendar.DAY_OF_MONTH) + "//" +
		this.postTime.get(Calendar.MONTH) + "//" +
		this.postTime.get(Calendar.YEAR);
		return toReturn;
	}

	/**
	 * @see
	 * 		UIMessage#getTime()
	 */
	public String getTime() {
		String toReturn = this.postTime.get(Calendar.HOUR_OF_DAY) + ":" +
		this.postTime.get(Calendar.MINUTE) + ":" +
		this.postTime.get(Calendar.SECOND);
		return toReturn;
	}

	/**
	 * 
	 * @return
	 * 		A collection which contains the ids of this message replies
	 */
	public Collection<Long> getReplies() {
		return this.repliesIDs;
	}	

	public long getFatherID() {
		return this.fatherID;
	}
	
	// setters
	
	/**
	 * Sets the title of the message to be the given one
	 * 
	 * @param title
	 * 		The new title of the message
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Sets the content of the message to be the given one
	 * 
	 * @param content
	 * 		The new content of the message
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	/**
	 * Sets the posting time of the message to be the given one
	 * 
	 * @param time
	 * 		The time to which the message posting-time attribute should be updated 
	 */
	private void setPostTime(GregorianCalendar time) {
		this.postTime = time;
	}

	// methods
	
	/**
	 * 
	 * Updates the message with the given parameters
	 * 
	 * @param newTitle
	 * 		The new title of the message
	 * @param newContent
	 * 		The new content of the message
	 */
	public void updateMe(String newTitle, String newContent) {
		this.setTitle(newTitle);
		this.setContent(newContent);
	}

	/**
	 * Adds a new reply id to the message
	 * 
	 * @param replyID
	 * 		The new reply id which should be added to the message
	 */
	public void addReply(final long replyID) {		
		this.repliesIDs.add(replyID);
	}

	/**
	 * Removes the given id from the message replies collection
	 * 
	 * @param replyID
	 * 		The id of the reply which should be removed from this message
	 */
	public void deleteReply(final long replyID) {
		this.repliesIDs.remove(replyID);
	}

	/**
	 * @see
	 * 		UIMessage#toString()
	 */
	public String toString() {
		return this.getMessageID() + "\t" + this.getAuthorID() + "\t" + this.getTitle() + "\t" + 
		this.getContent();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ForumMessage) {
			ForumMessage tMessage = (ForumMessage)o;
			return tMessage.getMessageID() == this.getMessageID() &&
				tMessage.getAuthorID() == this.getAuthorID() &&
				tMessage.getTitle().equals(this.getTitle()) &&
				tMessage.getContent().equals(this.getContent());
		}
		return false;
	}
	
}