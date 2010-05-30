package forum.server.domainlayer.interfaces;

import java.util.Collection;

import forum.server.domainlayer.user.Permission;

/**
 * This interface is used to present the data of a ForumUser object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the User state.
 */
public interface UIUser {
	
	/**
	 * @return
	 * 		The unique id of the user
	 */
	public long getID();
	
	/**
	 * 
	 * @param permissionToCheck
	 * 		The permission which should be checked.
	 * 
	 * @return
	 * 		Whether the user is allowed to perform an operation specified by the given permission.
	 * 
	 */
	public boolean isAllowed(final Permission permissionToCheck);
	
	/**
	 * 
	 * @return
	 * 		The collections of permissions which are assigned to this user
	 */
	public Collection<Permission> getPermissions();
}
