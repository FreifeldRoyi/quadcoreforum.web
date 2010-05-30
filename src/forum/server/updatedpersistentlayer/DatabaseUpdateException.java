/**
 * 
 */
package forum.server.updatedpersistentlayer;

/**
 * @author sepetnit
 *
 */
public class DatabaseUpdateException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2329050388469714022L;

	public DatabaseUpdateException() {
		super("Can't connect to the database");
	}
}
