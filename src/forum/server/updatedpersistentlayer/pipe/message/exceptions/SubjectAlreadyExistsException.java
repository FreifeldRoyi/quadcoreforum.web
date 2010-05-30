/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.message.exceptions;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class SubjectAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 7545807122791508691L;

	public SubjectAlreadyExistsException(long subjectID) {
		super("A subject with the subject id " + subjectID + " already exists!");
	}
	
	public SubjectAlreadyExistsException(String subjectName) {
		super("A subject with the subject name " + subjectName + " already exists!");
	}

}
