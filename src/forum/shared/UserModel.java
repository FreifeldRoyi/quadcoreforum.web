/**
 * 
 */
package forum.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;

import forum.shared.Permission;

/**
 * @author sepetnit
 *
 */
public class UserModel implements ModelData, Serializable {

	private static final long serialVersionUID = 8075093857428645727L;

	public enum UserType implements Serializable {
		GUEST, MEMBER, MODERATOR, ADMIN
	}
	
	private Map<String, Object> properties;

	public UserModel() 
	{ 
		this.properties = new HashMap<String, Object>();
	}
	
	public UserModel(long userID, String username, String lastName, String firstName, String email,
			String type, Collection<Permission> permissions) 
	{
		this();
		this.setID(userID);
		this.setUsername(username);
		this.setLastName(lastName);
		this.setFirstName(firstName);
		this.setEmail(email);
		this.setPermissions(permissions);
		this.setUserType(type);
	}
	
	public UserModel(long userID, Collection<Permission> permissions) {
		this(userID, null, null, null,  null, "GUEST", permissions);
	}
	
	public boolean isAllowed(final Permission permissionToCheck) {
		return this.getPermissions().contains(permissionToCheck);
	}

	public void setPermissions(Collection<Permission> permissions)
	{
		this.set("permissions", permissions);
	}
	
	public Collection<Permission> getPermissions()
	{
		return this.get("permssions");
	}
	
	public void setUserType(String type)
	{
		this.set("type", type);
	}
	
	public long getID() 
	{
		return this.get("userID");
	}
	
	public boolean isGuest() 
	{
		return this.getID() < 0;
	}
	
	public String getLastAndFirstName() 
	{
		return this.getLastName() + " " + this.getFirstName();
	}
	
	public String getFirstName() 
	{
		return this.get("firstName");
	}
	
	public String getLastName() 
	{
		return this.get("lastName");
	}
	
	public String getEmail() 
	{
		return this.get("email");
	}
	
	public String getUsername() 
	{
		return this.get("username");
	}
	
	public UserType getType() 
	{
		return UserType.valueOf((String)this.get("type"));
	}

	public void setID(long id) 
	{
		this.set("userID", id);
	}

	public void setUsername(String username) 
	{
		this.set("username", username);
	}
	
	public void setFirstName(String firstName) 
	{
		this.set("firstName", firstName);
	}
	
	public void setLastName(String lastName) 
	{
		this.set("lastName",lastName);
	}
	
	public void setEmail(String email) 
	{
		this.set("email", email);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(String property) 
	{
		if (this.properties == null)
			this.properties = new HashMap<String, Object>();
		
		return (X) this.properties.get(property);
	}

	@Override
	public Map<String, Object> getProperties() 
	{
		return this.properties;
	}

	@Override
	public Collection<String> getPropertyNames() 
	{
		return this.properties.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X remove(String property) 
	{
		if (this.properties == null)
			this.properties = new HashMap<String, Object>();
		
		return (X) this.properties.remove(property);
	}

	@Override
	public <X> X set(String property, X value) 
	{
		if (this.properties == null)
			this.properties = new HashMap<String, Object>();
		
		this.properties.put(property, value);
		return value;
	}
}
