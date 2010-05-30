package forum.server.domainlayer.interfaces;

/**
 * This interface is used to present the data of a ForumSubject object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Subject state.
 */
public interface UISubject {
	/**
	 * @return
	 * 		The unique id of the subject
	 */
	public long getID();

	/**
	 * @return
	 * 		The name of the subject
	 */
	public String getName();
	
	/**
	 * 
	 * @return
	 * 		The description of the subject
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return
	 * 		The number of this subject sub-subjects
	 */
	public long getNumOfSubSubjects();
	
	/**
	 * 
	 * @return
	 * 		The number of this subject threads
	 */
	public long getNumOfThreads();
	/**
	 * 
	 * @return
	 * 		A string representation of this subject
	 */
	public String toString();
	
	public long getFatherID();
}
