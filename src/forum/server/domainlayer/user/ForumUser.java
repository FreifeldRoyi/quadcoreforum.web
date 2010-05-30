/**
 * This class represents a forum user - either a guest or a member
 * 
 * The class is a base class of the forum member, its purpose is to handle a set of the user's permission
 * of operation he is allowed to perform
 */
package forum.server.domainlayer.user;

import java.util.*;

import forum.server.domainlayer.interfaces.UIUser;

/**
 * @author Vitali Sepetnitsky
 *
 */
public class ForumUser implements UIUser {
/*	public enum UserType {
		GUEST, MEMBER, ADMIN, MODERATOR
	}
*/	
	
	private long id;
	private Collection<Permission> permissions;
//	private UserType type;
	
	/**
	 * The class constructor
	 * 
	 * @param id
	 * 		The id of the constructed user
	 * @param permissions
	 * 		The initiali collection of permissions which are given to the user
	 */
	public ForumUser(final long id, final Collection<Permission> permissions) {
		this.id = id;
		this.permissions = permissions;
	}
	
	/**
	 * 
	 * The class constructor
	 * 
	 * Constructs a new user with an empty set of permissions
	 * 
	 * @param id
	 * 		The id of the new user
	 */
	public ForumUser(final long id) {
		this.id = id;
		this.permissions = new HashSet<Permission>();
	}
	
	// getters 
	
	/**
	 * @see
	 * 		UIUser#getID()
	 */
	public long getID() {
		return this.id;
	}

	/**
	 * @see
	 * 		UIUser#isAllowed(Permission)
	 */
	public boolean isAllowed(final Permission permissionToCheck) {
		return this.permissions.contains(permissionToCheck);
	}

	/**
	 * @see
	 * 		UIUser#getPermissions()
	 *
	 */
	public Collection<Permission> getPermissions() {
		return this.permissions;
	}

	// methods
	
	/**
	 * 
	 * This method overrides the standard equals method and 
	 * checks whether two users are the same one 
	 * 
	 * @see
	 * 		Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return (obj != null) && (obj instanceof ForumUser) && (((ForumUser)obj).getID() == 
			this.getID());
	}
	
	/**
	 * Adds a permission to this user existing permissions
	 */
	public void addPermission(final Permission permissionToAdd) {
		this.permissions.add(permissionToAdd);
	}
	
	/**
	 * Removes a permission from this user permissions collection
	 * 
	 * @param permissionToRemove
	 * 		The permission which should be removed from this user
	 */
	public void removePermission(final Permission permissionToRemove) {
		this.permissions.remove(permissionToRemove);
	}
	
	/**
	 * Sets the permissions set of the current user to be the given permissions set
	 * 
	 * @param permissionsToSet
	 * 		The permissions which should be assigned to the current user instead of his
	 * 		current permissions
	 */
	public void setPermissions(final Collection<Permission> permissionsToSet) {
		this.permissions = permissionsToSet;
	}
}
