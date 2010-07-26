/**
 * 
 */
package forum.shared;

import java.io.Serializable;
import java.util.Collection;

import forum.shared.Permission;

/**
 * @author sepetnit
 *
 */
public class UserModel implements Serializable {

	private static final long serialVersionUID = 8075093857428645727L;

	public enum UserType {
		GUEST, MEMBER, MODERATOR, ADMIN
	}
	
	private long userID;
	private String username;
	private String firstName;
	private String lastName;
	private Collection<Permission> permissions;
	private String email;
	private UserType type;

	public UserModel() { }
	
	public UserModel(long userID, String username, String lastName, String firstName, String email,
			String type, Collection<Permission> permissions) {
		this.userID = userID;
		this.username = username;
		this.lastName = lastName;
		this.firstName = firstName;
		this.email = email;
		this.permissions = permissions;
		this.type = UserType.valueOf(type);
	}
	
	public UserModel(long userID, Collection<Permission> permissions) {
		this(userID, null, null, null,  null, "GUEST", permissions);
	}

	public boolean isAllowed(final Permission permissionToCheck) {
		return this.permissions.contains(permissionToCheck);
	}

	public long getID() {
		return this.userID;
	}
	
	public boolean isGuest() {
		return this.userID < 0;
	}
	
	public String getLastAndFirstName() {
		return this.lastName + " " + this.firstName;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public UserType getType() {
		return this.type;
	}

	public void setID(long id) {
		this.userID = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
