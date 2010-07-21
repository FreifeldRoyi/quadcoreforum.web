/**
 * 
 */
package forum.shared.exceptions.message;

import java.io.Serializable;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class SubjectAlreadyExistsException extends Exception implements Serializable {

	private static final long serialVersionUID = 7545807122791508691L;
	
	private long subjectID;
	private String subjectName;
	
	public SubjectAlreadyExistsException() { }
	public SubjectAlreadyExistsException(long subjectID) {
		super("A subject with the subject id " + subjectID + " already exists!");
		this.subjectID = subjectID;
	}
	
	public SubjectAlreadyExistsException(String subjectName) {
		super("A subject with the subject name " + subjectName + " already exists!");
		this.subjectName = subjectName;
	}
	
	/**
	 * @return the subjectID
	 */
	public long getSubjectID() {
		return subjectID;
	}
	
	/**
	 * @param subjectID the subjectID to set
	 */
	public void setSubjectID(long subjectID) {
		this.subjectID = subjectID;
	}
	
	/**
	 * @return the subjectName
	 */
	public String getSubjectName() {
		return subjectName;
	}
	
	/**
	 * @param subjectName the subjectName to set
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
}
