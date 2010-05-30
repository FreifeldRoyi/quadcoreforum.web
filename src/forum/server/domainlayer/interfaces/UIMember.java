package forum.server.domainlayer.interfaces;

/**
 * This interface is used to present the data of a ForumUser object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Member state.
 */
public interface UIMember extends UIUser {
	
	/**
	 * @return
	 * 		The user-name of the member
	 */
	public String getUsername();

	/**
	 * @return
	 * 		The last name of the member
	 */
	public String getLastName();
	
	/**
	 * @return
	 * 		The first name of the member
	 */
	public String getFirstName();

	/**
	 * 
	 * @return
	 * 		The e-mail address of the member
	 */
	public String getEmail();
	
	/**
	 * 
	 * @return
	 * 		The number of messages posted by the member
	 */
	public int getPostsNumber();
	
	/**
	 * 
	 * @return
	 * 		A string representation of the member
	 */
	public String toString();

}