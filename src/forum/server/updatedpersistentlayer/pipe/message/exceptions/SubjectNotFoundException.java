/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.message.exceptions;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class SubjectNotFoundException extends Exception {

	private static final long serialVersionUID = -8637231750269406707L;

	public SubjectNotFoundException(long subjectID) {
		super("A subject with the subject id " + subjectID + " wasn't found!");
	}
}
