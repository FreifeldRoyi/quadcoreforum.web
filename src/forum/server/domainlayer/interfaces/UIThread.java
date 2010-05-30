package forum.server.domainlayer.interfaces;

/**
 * This interface is used to present the data of a ForumThread object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Thread state.
 */
public interface UIThread {
	/**
	 * @return
			The id of the thread
	 */
	public long getID();

	/**
	 * @return
	 * 		The topic of the thread
	 */
	public String getTopic();

	
	/**
	 * 
	 * @return
	 * 		The id of the thread's root message
	 */
	public long getRootMessageID();
	
	/**
	 * 
	 * @return
	 * 		The number of responses to the threads messages, this is the number of messages
	 * 		posted in the thread - 1 (its root message)
	 */
	public long getNumOfResponses();

	/**
	 * 
	 * @return
	 * 		The number of views of this thread
	 */
	public long getNumOfViews();
	
	public long getFatherID();
	
	/**
	 * 
	 * @return
	 * 		A string representation of this thread
	 */
	public String toString();
}
