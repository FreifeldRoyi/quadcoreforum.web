/**
 * 
 */
package forum.shared.exceptions.message;

import java.io.Serializable;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class SubjectNotFoundException extends Exception implements Serializable {
	private static final long serialVersionUID = -6575176913866619267L;

	private long id;
	
	public SubjectNotFoundException() {}
	
	public SubjectNotFoundException(long subjectID) {
		super("A subject with the subject id " + subjectID + " wasn't found!");
		this.id = subjectID;
	}
	
	public long getID() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
}
