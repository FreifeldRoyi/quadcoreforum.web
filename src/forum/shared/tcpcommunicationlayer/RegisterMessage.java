package forum.shared.tcpcommunicationlayer;

import java.io.Serializable;

/**
 * @author Lital Badash
 *
 */
public class RegisterMessage implements Serializable {

	private static final long serialVersionUID = -3267419208356408002L;

	/* The user-name of the user. */
	private String username;

	/* The password of the user. */
	private String password;

	/* The user last name. */
	private String lastname;

	/* The user first name. */
	private String firstname;

	/* The e-mail of the user. */
	private String email;

	public RegisterMessage() {}
	public RegisterMessage(final String username, final String password, final String lastname, 
			final String firstname, final String email) {
		this.setUsername(username);
		this.setPassword(password);		
		this.setFirstname(firstname);
		this.setLastname(lastname);
		this.setEmail(email);
	}

	// getters
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getFirstName() {
		return firstname;
	}
	
	public String getLastName() {
		return this.lastname;
	}
	
	public String getEmail() {
		return email;
	}
	
	// setters
	
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
