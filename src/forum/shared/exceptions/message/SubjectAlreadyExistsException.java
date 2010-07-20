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

	public SubjectAlreadyExistsException(long subjectID) {
		super("A subject with the subject id " + subjectID + " already exists!");
	}
	
	public SubjectAlreadyExistsException(String subjectName) {
		super("A subject with the subject name " + subjectName + " already exists!");
	}

}
