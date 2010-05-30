package forum.server.updatedpersistentlayer;

import java.util.*;


public class MemberType {
 private enum UserType{ 
	 MEMBER,
	 MODERATOR,
	 ADMIN
 }
    protected long userID;
 
    protected String username;
 
    protected String email; 
    protected String password; 
    protected String lastName;
    protected String firstName;
    protected UserType userType;
    protected Set<String> permissions;
    
    protected List<Long> postedMessagesIDs;
  

    /**
     * Gets the value of the userID property.
     * 
     */
    public long getUserID() {
        return userID;
    }
    
    /**
     * Sets the value of the userID property.
     * 
     */
    public void setUserID(long value) {
        this.userID = value;
    }
    
    
    /**
     * Gets the value of the userType property.
     * 
     */
    public String getUserType() {
    	if (userType == null)
    		return UserType.MEMBER.toString();
        return userType.toString();
    }
    
    /**
     * Sets the value of the userType property.
     * 
     */
    public void setUserType(String value) {
        this.userType = UserType.valueOf(value);
    }


    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the eMail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the eMail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }
    
    public Set<String> getPermissions() {
        return this.permissions;
    }
    
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
    
    /**
     * Gets the value of the postedMessagesIDs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the postedMessagesIDs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPostedMessagesIDs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getPostedMessagesIDs() {
        if (postedMessagesIDs == null) {
            postedMessagesIDs = new ArrayList<Long>();
        }
        return this.postedMessagesIDs;
    }

   
   

}
